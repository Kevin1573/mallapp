package com.wx.common.model.request;

import lombok.Data;

@Data
public class LoginRequest {

    private String token;

    private String loginCode;

    private String phoneCode;

    /**
     * 邀请人id
     */
    private Long inviteUserId;}
