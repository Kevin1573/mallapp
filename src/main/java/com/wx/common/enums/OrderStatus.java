package com.wx.common.enums;

// 订单状态；1未支付，2已支付, 3代发货, 4待收货, 5已完成, 6退款中, 7已退款
public enum OrderStatus {
    WAITING_PAYMENT(1, "待支付"),
    PAID(2, "已支付"),
    WAITING_SHIPMENT(3, "待发货"),
    SHIPPED(4, "已发货"),
    COMPLETED(5, "已完成"),
    REFUNDING(6, "退款中"),
    REFUNDED(7, "已退货"),
    CANCEL(8, "已取消");

    //    WAITING_PAYMENT, // 10 等待支付
//    PAID, // 20 已支付
//    PAID_UNPACK, // 30 已支付 待发货
//    SHIPPED, // 40 已发货
//    RECEIVED, //  待收货
//    COMPLETED, //  50 已完成
//    REFUNDING, // 60 退款中
//    RETURNED, // 70 已退货
//    ;
    private final int code;
    private final String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 添加状态码转换方法
    public static OrderStatus fromCode(Integer code) {
        if (code == null) return null;
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
