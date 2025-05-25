package com.wx.service;

import com.aliyun.oss.OSSException;
import com.wx.common.model.request.OssRequest;
import com.wx.common.model.response.OssConfigResponse;

import java.io.InputStream;

public interface OssService {

    /**
     * 获取oss登录信息
     *
     * @return OssConfigResponse
     * @throws Exception
     */
    OssConfigResponse getOssConfig(OssRequest request) throws Exception;

    /**
     * 流式上传文件到OSS
     * @param inputStream 输入流（调用方负责关闭）
     * @param objectName OSS存储路径（如：mall/image.jpg）
     * @param bucket 目标存储桶
     * @param contentType 内容类型（如：image/jpeg）
     * @return 可访问的URL
     */
    String uploadStream(InputStream inputStream,
                        String objectName,
                        String bucket,
                        String contentType) throws OSSException;

    String uploadFile(String localFilePath, String ossObjectPath) throws OSSException;


}
