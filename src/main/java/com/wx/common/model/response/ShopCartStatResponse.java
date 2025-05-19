package com.wx.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopCartStatResponse {
    // 待付款
    private Long waitPay;
    // 待发货
    private Long waitSend;
    // 待收货
    private Long waitReceive;
    // 已完成
    private Long complete;
    // 退款
    private Long refund;
}
