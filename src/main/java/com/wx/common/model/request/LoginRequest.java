package com.wx.common.model.request;

import lombok.Data;

@Data
public class LoginRequest {

    private String userName;

    private String password;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像链接
     */
    private String headUrl;

    /**
     * 邀请人id
     */
    private String from;
}
