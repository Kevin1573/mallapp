package com.wx.common.model.response;

import lombok.Data;

@Data
public class LoginResonse {

    private Long userId;

    /**
     * 登陆标识
     */
    private String token;

    private String nickName;

    private String phone;

    private String headUrl;

    /**
     * 用户职位
     */
    private String position;

}
