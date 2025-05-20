package com.wx.common.model.response;

import lombok.Data;

import java.util.List;

@Data
public class QueryGoodsModel {

    private Long id;

    /**
     * 商品图片
     */
    private List<String> goodsPics;

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

    private List<String> brandPics;

}
