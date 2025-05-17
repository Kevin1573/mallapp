package com.wx.common.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

// wx支付请求
@Data
@Builder
@AllArgsConstructor
public class WxPaymentRequest {
    @NotNull(message = "金额不能为空")
    @Min(value = 1, message = "金额必须大于0")
    private Integer amountTotal;

    @Size(max = 128, message = "描述过长")
    private String description;

    private String outTradeNo;

    private String notifyUrl;

    private String appid;
    // 后续扩展字段
    private String mchid; // 多商户预留
}