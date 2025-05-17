package com.wx.service;

import com.wx.common.model.request.EditPasswordRequest;
import com.wx.common.model.request.LoginRequest;
import com.wx.common.model.request.TokenRequest;
import com.wx.common.model.request.UserProfileRequest;
import com.wx.common.model.response.LoginResonse;

public interface LoginService {

    /**
     * 登陆接口
     *
     * @param request 前端获取到的登陆code
     * @return 用户信息
     */
    LoginResonse login(LoginRequest request);

    /**
     * 注册接口
     *
     * @param request 前端获取到的登陆code
     */
    void register(LoginRequest request);

    /**
     * 更新用户信息
     *
     * @param request 用户信息
     */
    void updateUserProfile(UserProfileRequest request);

    /**
     * 根据token获取用户信息
     *
     * @param request token信息
     * @return 用户信息
     */
    LoginResonse getUserInfoByToken(TokenRequest request);

    /**
     * 修改密码
     *
     * @param request
     */
    void editPassword(EditPasswordRequest request);
}
