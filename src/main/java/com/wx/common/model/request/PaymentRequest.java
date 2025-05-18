package com.wx.common.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

// 支付请求
@Data
@Builder
@AllArgsConstructor
public class PaymentRequest {
    @Size(max = 128, message = "订单标题过长")
    private String subject;
    private String tradeNo;
    private String from;
}