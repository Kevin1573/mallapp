package com.wx.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BestSellingGoods {
    private Long id;       // 改为 Long 类型
    private String name;
    private String goodsPic;
    private String goodsTitle;
    private BigDecimal price;  // 改为 BigDecimal
    private Integer sales;     // 改为 Integer
}
