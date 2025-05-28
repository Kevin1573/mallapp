package com.wx.common.model.request;

import lombok.Data;

@Data
public class OrderListRequest {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String token;
    private String tradeNo;
    private Integer status;
}