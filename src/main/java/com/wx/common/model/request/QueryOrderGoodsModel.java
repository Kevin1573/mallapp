package com.wx.common.model.request;

import lombok.Data;

@Data
public class QueryOrderGoodsModel {

    private Long id;

    /**
     * 订单图片
     */
    private String goodsPic;

    /**
     * 订单描述
     */
    private String goodsDes;

    private String goodsName;

    private Double goodsPrice;

    private Long num;
}
