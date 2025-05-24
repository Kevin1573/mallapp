package com.wx.common.model.response;

import lombok.Data;

@Data
public class CompanyConfigResponse {

    private String companyName;

    private String companyPhone;

    private String companyAddr;

    private String programUrl;

    private String wechatUrl;

    private String email;
}
