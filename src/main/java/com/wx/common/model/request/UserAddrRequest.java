package com.wx.common.model.request;

import lombok.Data;

@Data
public class UserAddrRequest {

    private Long id;

    private String token;

    private String province;
    private String city;
    private String area;
    private String detail;

    private String name;

    private String phone;

    private String isDefault;
}
