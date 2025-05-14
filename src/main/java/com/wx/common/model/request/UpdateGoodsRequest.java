package com.wx.common.model.request;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateGoodsRequest {

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

    /**
     * 商品类型
     */
    private String typeId;

    private Date createTime;

    private Date modifyTime;

    /**
     * 商品扩展图片
     */
    private String ext;

    private Double weight;

}
