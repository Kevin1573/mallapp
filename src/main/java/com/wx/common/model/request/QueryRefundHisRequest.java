package com.wx.common.model.request;

import lombok.Data;

@Data
public class QueryRefundHisRequest {

    private String status;

    private Long limit;

    private Long page;
}
