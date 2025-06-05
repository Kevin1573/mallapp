package com.wx.common.model.request;

import lombok.Data;

@Data
public class ShopModel {
    private Long id;
    private String name;

    private String pic;
    private String title;

    private Double price;

    private String goodsUnit;

}
