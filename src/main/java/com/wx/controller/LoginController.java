package com.wx.controller;

import com.alibaba.fastjson.JSON;
import com.wx.common.exception.BizException;
import com.wx.common.model.Response;
import com.wx.common.model.request.EditPasswordRequest;
import com.wx.common.model.request.LoginRequest;
import com.wx.common.model.request.TokenRequest;
import com.wx.common.model.request.UserProfileRequest;
import com.wx.common.model.response.LoginResponse;
import com.wx.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public Response<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            return Response.success(loginService.login(request));
        } catch (Exception e) {
            log.error("Login exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Login exception");
        }
    }

    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    public Response<?> register(@RequestBody LoginRequest request) {
        try {
            loginService.register(request);
            return Response.success();
        } catch (BizException be) {
            return Response.failure(be.getMessage());
        } catch (Exception e) {
            log.error("Register exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("注册出错了...");
        }
    }

    @RequestMapping(value = "/updateUserProfile", method = {RequestMethod.POST})
    public Response updateUserProfile(@RequestBody UserProfileRequest request) {
        try {
            loginService.updateUserProfile(request);
            return Response.success();
        } catch (Exception e) {
            log.error("UpdateUserProfile exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("UpdateUserProfile exception");
        }
    }

    @RequestMapping(value = "/getUserInfoByToken", method = {RequestMethod.POST})
    public Response<LoginResponse> getUserInfoByToken(@RequestBody TokenRequest request) {
        try {
            return Response.success(loginService.getUserInfoByToken(request));
        } catch (Exception e) {
            log.error("getUserInfoByToken exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("getUserInfoByToken exception");
        }
    }

    @RequestMapping(value = "/editPassword", method = {RequestMethod.POST})
    public Response editPassword(@RequestBody EditPasswordRequest request) {
        try {
            loginService.editPassword(request);
            return Response.success();
        } catch (Exception e) {
            log.error("editPassword exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("editPassword exception");
        }
    }

}
