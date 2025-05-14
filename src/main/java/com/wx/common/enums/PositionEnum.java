package com.wx.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PositionEnum {

    VIP_CUSTOMERS("vipCustomers", "贵宾顾客"),
    INTERNSHIP_PARTNER("internshipPartner", "Q1经销商"),
    JUNIOR_PARTNER("juniorPartner", "Q2经销商"),
    INTERMEDIATE_PARTNER("intermediatePartner", "M1合伙人"),
    SENIOR_PARTNER("seniorPartner", "M7合伙人"),
    CORE_PARTNER("corePartner", "超级管理员"),
    ;

    public static PositionEnum getEnumByCode (String code) {
        for (PositionEnum positionEnum : PositionEnum.values()) {
            if (positionEnum.getCode().equals(code)) {
                return positionEnum;
            }
        }
        return null;
    }

    private String code;

    private String desc;
}
