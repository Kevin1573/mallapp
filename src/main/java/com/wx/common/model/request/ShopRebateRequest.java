package com.wx.common.model.request;

import lombok.Data;

@Data
public class ShopRebateRequest {
    private Long id;
    private String token;
    private String description;
    private String positionCode;
    private Double ratio;
    private String fromMall;
}
