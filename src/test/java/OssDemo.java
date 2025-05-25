import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.internal.Mimetypes;
import com.aliyun.oss.model.*;
import com.wx.common.utils.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OssDemo {
    public static void main(String[] args) {
        OSS ossClient = new OSSClientBuilder().build(Constants.ENDPOINT2, Constants.ACCESS_KEY_ID, Constants.ACCESS_KEY_SECRET);
        try {
            // 创建Bucket。
//            ossClient.createBucket(Constants.BUCKET);
            // 创建Object的Metadata。
            ObjectMetadata meta = new ObjectMetadata();
            // 设置上传内容类型。
            meta.setContentType("image/jpeg");
            // 创建PutObject请求。
            File file = new File("C:\\Users\\kevin\\Pictures\\微信图片_2025-05-22_210124_554.jpg");
            InputStream inputStream = new FileInputStream(file);
//            ossClient.putObject(Constants.BUCKET, "微信图片_2025.jpg", inputStream, meta);


            PutObjectRequest putObjectRequest = new PutObjectRequest(Constants.BUCKET, "mall/微信图片_2025.jpg", inputStream, meta);
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            System.out.println("ETag: " + putObjectResult.getETag());
            System.out.println("RequestId: " + putObjectResult.getRequestId());

            // https://mall-app-123.oss-cn-hangzhou.aliyuncs.com/mall/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_2025.jpg
            String fileRemoteUri = "https://" + Constants.BUCKET + "." + Constants.ENDPOINT2 + "/" + "mall/微信图片_2025.jpg";
            // 对文件路径进行转码
            String fileRemoteUriEncode = java.net.URLEncoder.encode(fileRemoteUri, "UTF-8");
            System.out.println("fileRemoteUriEncode: " + fileRemoteUriEncode);
            System.out.println("上传成功");
        }
        catch (FileNotFoundException fe) {
            fe.printStackTrace();
        }
        catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main2(String[] args) throws Exception {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = Constants.ENDPOINT; // "https://oss-cn-hangzhou.aliyuncs.com";
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = Constants.BUCKET; //"examplebucket";
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String objectName = "微信图片_2025.jpg";
        // 待上传本地文件路径。
        String filePath = "\"C:\\Users\\kevin\\Pictures\\微信图片_2025-05-22_210124_554.jpg\"";
        // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。
        String region = Constants.REGION; // "cn-hangzhou";

        // 创建OSSClient实例。
        // 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            // 创建InitiateMultipartUploadRequest对象。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName);

            // 创建ObjectMetadata并设置Content-Type。
            ObjectMetadata metadata = new ObjectMetadata();
            if (metadata.getContentType() == null) {
                metadata.setContentType(Mimetypes.getInstance().getMimetype(new File(filePath), objectName));
            }
            System.out.println("Content-Type: " + metadata.getContentType());

            // 将metadata绑定到上传请求中。
            request.setObjectMetadata(metadata);

            // 初始化分片。
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId。
            String uploadId = upresult.getUploadId();

            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
            List<PartETag> partETags = new ArrayList<PartETag>();
            // 每个分片的大小，用于计算文件有多少个分片。单位为字节。
            // 分片最小值为100 KB，最大值为5 GB。最后一个分片的大小允许小于100 KB。
            // 设置分片大小为 1 MB。
            final long partSize = 1 * 1024 * 1024L;   

            // 根据上传的数据大小计算分片数。以本地文件为例，说明如何通过File.length()获取上传数据的大小。
            final File sampleFile = new File(filePath);
            long fileLength = sampleFile.length();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(objectName);
                uploadPartRequest.setUploadId(uploadId);
                // 设置上传的分片流。
                // 以本地文件为例说明如何创建FileInputStream，并通过InputStream.skip()方法跳过指定数据。
                InputStream instream = new FileInputStream(sampleFile);
                instream.skip(startPos);
                uploadPartRequest.setInputStream(instream);
                // 设置分片大小。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partETags.add(uploadPartResult.getPartETag());

                // 关闭流
                instream.close();
            }

            // 创建CompleteMultipartUploadRequest对象。
            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);

            // 完成分片上传。
            CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println("上传成功，ETag：" + completeMultipartUploadResult.getETag());

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught a ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}