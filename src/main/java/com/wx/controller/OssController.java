package com.wx.controller;

import cn.hutool.core.lang.generator.UUIDGenerator;
import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSException;
import com.wx.common.model.Response;
import com.wx.common.model.request.OssRequest;
import com.wx.common.model.response.OssConfigResponse;
import com.wx.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;

@RestController
@RequestMapping(value = "/oss")
@Slf4j
public class OssController {

    @Autowired
    private OssService ossService;

    @RequestMapping(value = "/getConfig", method = {RequestMethod.POST})
    public Response<OssConfigResponse> orderRepair(@RequestBody OssRequest request) {
        try {
            return Response.success(ossService.getOssConfig(request));
        } catch (Exception e) {
            log.error("Oss getConfig exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Oss getConfig exception");
        }
    }

    @RequestMapping(value = "/upload2", method = {RequestMethod.POST})
    public Response<String> upload2(MultipartFile file, @RequestBody OssRequest request) {
        try {
            // 获取上传文件的路径
            String fileName = file.getOriginalFilename();
            String filePath = ossService.uploadFile(fileName, "mall/" + new UUIDGenerator().next() + ".jpg");
            return Response.success(filePath);
        } catch (Exception e) {
            log.error("Oss getConfig exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Oss getConfig exception");
        }
    }


    // 修改后的上传接口
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Response<String> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("bucket") String bucket,
            @RequestParam("category") String category) {
        try {
            // 1. 基础校验
            if (file.isEmpty()) {
                return Response.failure("文件不能为空");
            }

            // 2. 生成安全文件名
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileExtension = FilenameUtils.getExtension(originalFilename);
            String objectName = String.format("%s/%s.%s",
                    category,
                    UUID.randomUUID(),
                    defaultIfBlank(fileExtension, "jpg"));

            // 3. 使用try-with-resources自动关闭流
            try (InputStream fileStream = file.getInputStream()) {
                String fileUrl = ossService.uploadStream(
                        fileStream,
                        objectName,
                        bucket,
                        file.getContentType()
                );
                return Response.success(fileUrl);
            }
        } catch (OSSException e) {
            log.error("OSS上传失败: {}", e.getErrorMessage(), e);
            return Response.failure("文件存储服务异常");
        } catch (IOException e) {
            log.error("文件流读取失败", e);
            return Response.failure("文件读取失败");
        } catch (Exception e) {
            log.error("系统异常", e);
            return Response.failure("系统处理异常");
        }
    }

    @PostMapping("/uploadStream")
    public Response<String> uploadStream(@RequestPart("file") MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            String url = ossService.uploadStream(
                    stream,
                    "mall/" + UUID.randomUUID() + ".jpg",
                    "mall-app-123",
                    file.getContentType()
            );
            return Response.success(url);
        } catch (OSSException e) {
            return Response.failure("文件存储失败");
        } catch (IOException e) {
            return Response.failure("文件流读取失败");
        }
    }

}
