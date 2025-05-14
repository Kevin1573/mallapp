package com.wx.common.model.request;

import lombok.Data;

@Data
public class AddShoppingCarRequest {

    private String token;

    private Long goodsId;

    private Long id;

    private Long num;
}
