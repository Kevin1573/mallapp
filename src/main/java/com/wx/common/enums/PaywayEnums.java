package com.wx.common.enums;

public enum PaywayEnums {
    ALIPAY("ALIPAY", "支付宝"),
    WECHAT("WECHAT", "微信"),
    ;

    private String code;
    private String desc;

    PaywayEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}