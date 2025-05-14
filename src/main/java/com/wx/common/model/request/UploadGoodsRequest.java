package com.wx.common.model.request;

import lombok.Data;

@Data
public class UploadGoodsRequest {

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

    private Long typeId;

    private String type;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 扩展图片列表
     */
    private String extList;

    private Double weight;
}
