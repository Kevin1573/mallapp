package com.wx.common.model.response;

import lombok.Data;

@Data
public class OssConfigResponse {

    private String region;

    private String accessKeyId;

    private String accessKeySecret;

    private String stsToken;

    private String bucket;


}
