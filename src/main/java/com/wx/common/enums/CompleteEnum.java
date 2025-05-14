package com.wx.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompleteEnum {

    FALSE(1, "未完成"),
    TRUE(2, "已完成"),
    ;

    private Integer code;

    private String desc;
}
