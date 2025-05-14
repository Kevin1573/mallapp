package com.wx.common.model.request;

import lombok.Data;

@Data
public class QueryUserByPhoneRequest {

    private String phone;

    /**
     * 是不是会员，1是 2不是
     */
    private Integer isMember;
}
