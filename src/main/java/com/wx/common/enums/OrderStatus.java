package com.wx.common.enums;

public enum OrderStatus {
    WAITING_PAYMENT, // 等待支付
    PAID, // 已支付
    PACKED, // 已打包
    SHIPPED, // 已发货
    RETURNED, // 已退货
    CANCELLED, // 已取消
    COMPLETED; //  已完成
}
