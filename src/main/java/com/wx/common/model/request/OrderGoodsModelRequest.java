package com.wx.common.model.request;

import lombok.Data;

@Data
public class OrderGoodsModelRequest {

    /**
     * 商品id
     */
    private Long goodsId;


    /**
     * 商品数量
     */
    private Long num;
}
