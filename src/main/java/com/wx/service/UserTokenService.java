package com.wx.service;

public interface UserTokenService {

    /**
     * 根据token获取用户openid
     *
     * @param token 登陆标识
     * @return openid
     */
    String getOpenidByToken(String token);
}
