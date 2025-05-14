package com.wx.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MaterialLibraryEnum {

    USAGRE("usage", "产品搭配及使用方法"),
    THEORETICAL_KNOWLEDGE(" theoreticalKnowledge ", "理论知识图案库"),
    BUSINESS_CLASS("businessClass", "七巷里商学院课程"),
    VIP_STORY("vipStory", "贵宾顾客故事会"),
    ENTERPRENEURSHIP("entrepreneurship", "七巷里经销商创业会"),
    ;

    public static MaterialLibraryEnum getEnumByCode (String code) {
        for (MaterialLibraryEnum positionEnum : MaterialLibraryEnum.values()) {
            if (positionEnum.getCode().equals(code)) {
                return positionEnum;
            }
        }
        return null;
    }

    private String code;

    private String desc;
}
