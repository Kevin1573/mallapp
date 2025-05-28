package com.wx.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.sts20150401.Client;
import com.aliyun.sts20150401.models.AssumeRoleRequest;
import com.aliyun.sts20150401.models.AssumeRoleResponse;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.wx.common.exception.BizException;
import com.wx.common.model.request.OssRequest;
import com.wx.common.model.response.OssConfigResponse;
import com.wx.common.utils.Constants;
import com.wx.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Slf4j
@Service
public class OssServiceImpl implements OssService {

    private final Client client;
    private static final String endpoint;
    private static final String accessKeyId;
    private static final String accessKeySecret;
    private static final String bucketName;

    static {
        endpoint = Constants.ENDPOINT2;
        accessKeyId = Constants.ACCESS_KEY_ID;
        accessKeySecret = Constants.ACCESS_KEY_SECRET;
        bucketName = Constants.BUCKET;
    }

    public OssServiceImpl(Client client) {
        this.client = client;
    }

    @Override
    public OssConfigResponse getOssConfig(OssRequest request) throws Exception {
        // 请求阿里云api，获取登录的临时凭证
        AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest()
                .setRoleArn(Constants.ROLE_ARN)
                .setRoleSessionName(Constants.ROLE_SESSION_NAME);
        RuntimeOptions runtime = new RuntimeOptions();
        AssumeRoleResponse assumeRoleResponse = client.assumeRoleWithOptions(assumeRoleRequest, runtime);
        AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials credentials = assumeRoleResponse.getBody().getCredentials();

        // 封装返回结果
        OssConfigResponse response = new OssConfigResponse();
        response.setAccessKeyId(credentials.getAccessKeyId());
        response.setAccessKeySecret(credentials.getAccessKeySecret());
        response.setStsToken(credentials.getSecurityToken());
        response.setRegion(Constants.REGION);
        response.setBucket(request.getBucket());
        return response;
    }

    @Override
    public String uploadStream(InputStream inputStream,
                               String objectName, String bucket,
                               String contentType) throws OSSException {
        // 在方法开始处添加：
        if (inputStream == null) {
            throw new IllegalArgumentException("输入流不能为空");
        }
        OSS client = null;
        try {
            // 1. 创建动态OSS客户端
            client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 2. 设置对象元数据
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType(contentType);
            meta.setContentDisposition("inline");
            meta.setContentLength(inputStream.available());
            // 3. 检测输入流是否可重复读取
            if (!inputStream.markSupported()) {
                inputStream = new BufferedInputStream(inputStream);
            }

            // 4. 路径编码处理
            String encodedObjectName = Arrays.stream(objectName.split("/"))
                    .map(this::encodePathSegment)
                    .collect(Collectors.joining("/"));

            // 5. 构建上传请求
            PutObjectRequest putRequest = new PutObjectRequest(
                    bucket,  // 使用动态传入的bucket
                    encodedObjectName,
                    inputStream,
                    meta
            );

            // 6. 执行上传
            PutObjectResult result = client.putObject(putRequest);
            log.info("流式上传成功, Bucket: {}, Object: {}, ETag: {}",
                    bucket, encodedObjectName, result.getETag());

            // 7. 生成访问URL（有效期2小时）
            Date expiration = new Date(System.currentTimeMillis() + 7200 * 1000);
            return client.generatePresignedUrl(bucket, encodedObjectName, expiration).toString();

        } catch (OSSException e) {
            log.error("OSS上传失败 | Bucket: {} | Object: {} | 错误码: {}",
                    bucket, objectName, e.getErrorCode());
            throw e; // 保留原始异常栈
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    @Override
    public String uploadFile(String localFilePath, String ossObjectPath) throws OSSException {
        OSS client = null;
        try (InputStream inputStream = Files.newInputStream(new File(localFilePath).toPath())) {
            // 1. 创建动态OSS客户端（修复静态客户端导致的问题）
            client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 2. 设置对象元数据
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType(detectContentType(localFilePath));
            meta.setContentDisposition("inline");

            // 3. 路径编码处理
            String encodedObjectPath = Arrays.stream(ossObjectPath.split("/"))
                    .map(this::encodePathSegment)
                    .collect(Collectors.joining("/"));

            // 4. 构建上传请求
            PutObjectRequest putRequest = new PutObjectRequest(
                    bucketName,
                    encodedObjectPath,
                    inputStream,
                    meta
            );
//                    .withProgressListener(new ProgressListener() {
//                public void progressChanged(ProgressEvent progressEvent) {
//                    System.out.println("传输进度: " + progressEvent.getBytes());
//                }
//            });

            // 5. 执行上传
            PutObjectResult result = client.putObject(putRequest);
            log.info("文件上传成功, ETag: {}, RequestId: {}",
                    result.getETag(), result.getRequestId());

            // 6. 生成访问URL（有效期2小时）
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000 * 2);
            URL url = client.generatePresignedUrl(bucketName, encodedObjectPath, expiration);
            return url.toString();

        } catch (OSSException e) {
            handleOssException(e);
            throw new BizException("OSS服务异常", e);
        } catch (IOException e) {
            throw new BizException("文件读取失败", e);
        } finally {
            if (client != null) {
                client.shutdown(); // 确保每个客户端实例正确关闭
            }
        }
    }

    // 辅助方法：检测文件类型
    private String detectContentType(String filePath) {
        String contentType = null;
        try {
            contentType = Files.probeContentType(Paths.get(filePath));
        } catch (IOException e) {
            log.warn("内容类型检测失败", e);
        }
        return defaultIfEmpty(contentType, "application/octet-stream");
    }

    // 辅助方法：路径分段编码
    private String encodePathSegment(String segment) {
        try {
            return URLEncoder.encode(segment, "UTF-8")
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("编码失败", e);
        }
    }

//    @Override
//    public String uploadFile(String localFilePath, String ossObjectPath) throws OSSException {
//        try (InputStream inputStream = Files.newInputStream(new File(localFilePath).toPath())) {
//            // ... [原有元数据设置代码]
//
//            // 对ossObjectPath进行分段编码
//            String encodedObjectPath = Arrays.stream(ossObjectPath.split("/"))
//                    .map(part -> {
//                        try {
//                            return URLEncoder.encode(part, "UTF-8")
//                                    .replace("+", "%20"); // 替换+号为更安全的编码
//                        } catch (UnsupportedEncodingException e) {
//                            throw new RuntimeException(e);
//                        }
//                    })
//                    .collect(Collectors.joining("/"));
//
//            // 构建访问URL
//            return URLEncoder.encode(
//                    "https://" + bucketName + "." + endpoint + "/" + encodedObjectPath,
//                    "UTF-8"
//            );
//        } catch (Exception e) {
//            // ... [原有异常处理代码]
//            e.printStackTrace();
//        }
//        return null;
//    }

//
//    public String uploadFile(String localFilePath, String ossObjectPath) throws OSSException {
//
//        try (InputStream inputStream = Files.newInputStream(new File(localFilePath).toPath())) {
//            // 设置元数据
//            ObjectMetadata meta = new ObjectMetadata();
//            meta.setContentType("image/jpeg"); // 可根据实际文件类型扩展
//
//            // 构建上传请求
//            PutObjectRequest putRequest = new PutObjectRequest(
//                    bucketName,
//                    ossObjectPath,
//                    inputStream,
//                    meta
//            );
//
//            // 执行上传
//            PutObjectResult result = ossClient.putObject(putRequest);
//
//            // 构建访问URL
//            return URLEncoder.encode(
//                    "https://" + bucketName + "." + endpoint + "/" + ossObjectPath,
//                    "UTF-8"
//            );
//        } catch (Exception e) {
//            if (e instanceof OSSException) {
//                handleOssException((OSSException) e);
//            }
//            throw new RuntimeException("文件上传失败", e);
//        } finally {
//            ossClient.shutdown();
//        }
//    }

    private void handleOssException(OSSException oe) {
        System.err.println("OSS异常信息: " + oe.getErrorMessage());
        System.err.println("错误代码: " + oe.getErrorCode());
        System.err.println("请求ID: " + oe.getRequestId());
        System.err.println("主机ID: " + oe.getHostId());
    }


//    public static void main(String[] args) throws Exception {
//        // 请求阿里云api，获取登录的临时凭证
//        Config config = new Config();
//        config.setAccessKeyId(Constants.ACCESS_KEY_ID);
//        config.setAccessKeySecret(Constants.ACCESS_KEY_SECRET);
//        config.setEndpoint(Constants.ENDPOINT);
//        Client client = new Client(config);
//        AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest()
//                .setRoleArn(Constants.ROLE_ARN)
//                .setRoleSessionName(Constants.ROLE_SESSION_NAME);
//        RuntimeOptions runtime = new RuntimeOptions();
//        AssumeRoleResponse assumeRoleResponse = client.assumeRoleWithOptions(assumeRoleRequest, runtime);
//        AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials credentials = assumeRoleResponse.getBody().getCredentials();
//
//        // 封装返回结果
//        OssConfigResponse response = new OssConfigResponse();
//        response.setAccessKeyId(credentials.getAccessKeyId());
//        response.setAccessKeySecret(credentials.getAccessKeySecret());
//        response.setStsToken(credentials.getSecurityToken());
//        response.setRegion(Constants.REGION);
//        response.setBucket("mall-app-123");
//        System.out.println(JSON.toJSONString(response));
//    }


}
