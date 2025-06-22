package com.wx.miniapp.dto;

import lombok.Data;

@Data
public class CreatePaymentRequest {
    private String openid;
    private String orderNo;
    private int amount;
    private String description;
}