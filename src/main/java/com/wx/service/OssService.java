package com.wx.service;

import com.wx.common.model.request.OssRequest;
import com.wx.common.model.response.OssConfigResponse;

public interface OssService {

    /**
     * 获取oss登录信息
     *
     * @return OssConfigResponse
     * @throws Exception
     */
    OssConfigResponse getOssConfig(OssRequest request) throws Exception;

}
