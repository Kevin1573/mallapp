package com.wx.common.model.response;

import lombok.Data;

@Data
public class MatchAddrResponse {
    private String province;

    private String city;

    private String area;

    private String detail;

    private String name;

    private String phone;
}
