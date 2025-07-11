package com.wx.common.model.request;

import lombok.Data;

@Data
public class UserProfileRequest {

    private Long id;

    private String token;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像链接
     */
    private String headUrl;

    /**
     * 用户地址
     */
    private String addr;

    private Long position;

    private String source;

    private Integer page;

    private Integer pageSize;
}
