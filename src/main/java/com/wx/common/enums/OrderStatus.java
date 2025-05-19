package com.wx.common.enums;

// 订单状态；1未支付，2已支付, 3代发货, 4待收货, 5已完成, 6退款中, 7已退款
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
