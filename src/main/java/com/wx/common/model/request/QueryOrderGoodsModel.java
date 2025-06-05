package com.wx.common.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QueryOrderGoodsModel {

    private Long id;
    private String brand;
    private String category;
    private String description;
    private Boolean firstGoods;
    private String goodsPic;
    private String goodsTitle;
    private String name;
    private BigDecimal price;
    private Long sales; // 销量
    private Long num;
    public QueryOrderGoodsModel() {}
    @JsonCreator // 方案一：带参构造+注解
    public QueryOrderGoodsModel(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("num") Long num) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.num = num;
    }
}
