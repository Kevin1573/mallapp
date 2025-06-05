package com.wx.common.model.response;

import lombok.Data;

@Data
public class QueryGoodsModel {

    private Long id;

    /**
     * 商品图片
     */
    private String goodsPic;

    private String goodsTitle;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 价格，单位为元
     */
    private Double price;

    private Long typeId;

    private String name;

    private String ext;

    private String goodsUnit;

    private Boolean firstGoods;

    private String brandPic;
    private Integer recommendGoods;
}
