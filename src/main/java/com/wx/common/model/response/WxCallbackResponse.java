package com.wx.common.model.response;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

// 回调验证
@Data
public class WxCallbackResponse {
    @NotBlank
    private String mchid;
    @NotBlank
    private String outTradeNo;
    @NotBlank
    private String transactionId;
    @Min(1)
    private Integer totalAmount;
}