package com.wx.common.model.request;

import lombok.Data;

@Data
public class PageQueryUserRequest {

    private Long limit;

    private Long page;

    private String phone;
}
