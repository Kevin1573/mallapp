package com.wx.common.enums;

import lombok.Getter;

public enum PayWayEnums {
    WECHAT("WECHAT", "微信", 1),
    ALIPAY("ALIPAY", "支付宝", 2),
    ;

    @Getter
    private final String code;
    @Getter
    private final String desc;
    @Getter
    private final Integer value;

    PayWayEnums(String code, String desc, Integer i) {
        this.code = code;
        this.desc = desc;
        this.value = i;
    }

}