package com.wx.common.enums;

public enum OrderStatus {
    WAITING_PAYMENT, // 等待支付
    PAID, // 已支付
    PAID_UNPACK, // 已支付 待发货
    SHIPPED, // 已发货
    RECEIVED, // 待收货
    COMPLETED, //  已完成
    REFUNDING, // 退款中
    RETURNED, // 已退货
    ;
}
