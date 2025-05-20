package com.wx.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopConfigResponse {
    private String shopName;
    private double freight; // 运费
    private double discount;  // 折扣%
}
