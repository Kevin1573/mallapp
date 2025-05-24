package com.wx.common.model.response;

import lombok.Data;

@Data
public class QueryCarOrdersResponse {

    private Long id;

    private Long goodsId;

    /**
     * 商品图片
     */
    private String goodsPic;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品单价
     */
    private Double price;

    /**
     * 总价格，单位为元
     */
    private Double totalPrice;

    /**
     * 商品名称
     */
    private String name;

    private Long num;

    private String specifications;
}
