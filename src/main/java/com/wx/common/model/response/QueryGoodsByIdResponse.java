package com.wx.common.model.response;

import lombok.Data;

import java.util.Date;

@Data
public class QueryGoodsByIdResponse {

    private Long id;

    /**
     * 商品图片
     */
    private String goodsPic;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 价格，单位为元
     */
    private Double price;

    /**
     * 商品名称
     */
    private String name;

    private Date createTime;

    private Date modifyTime;

    /**
     * 商品扩展图片
     */
    private String ext;
}
