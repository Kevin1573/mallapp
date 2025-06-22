package com.wx.miniapp.controller;

import com.wx.common.model.ApiResponse;
import com.wx.miniapp.dto.AuthRegisterRequest;
import com.wx.miniapp.dto.PaymentAuthRequest;
import com.wx.miniapp.service.MiniAppAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/miniapp/auth")
public class MiniAppAuthController {

    @Autowired
    private MiniAppAuthService miniAppAuthService;

    /**
     * 微信小程序登录接口
     * @return 登录结果
     */
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody PaymentAuthRequest request) {
        return miniAppAuthService.login(request.getCode(), request.getEncryptedData(), request.getIv());
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody AuthRegisterRequest request) {
        return miniAppAuthService.register(request);
    }

    /**
     * 检查登录状态
     * @param token 用户token
     * @return 检查结果
     */
    @GetMapping("/checkSession")
    public ApiResponse<?> checkSession(@RequestHeader("Authorization") String token) {
        return miniAppAuthService.checkSession(token);
    }

}
