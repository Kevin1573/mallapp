package com.wx.common.model.request;

import lombok.Data;

@Data
public class QueryGoodsByIdRequest {

    private String goodsUnit;

    private String token;
}
