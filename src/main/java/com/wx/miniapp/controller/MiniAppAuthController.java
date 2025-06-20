package com.wx.miniapp.controller;

import com.wx.common.model.ApiResponse;
import com.wx.miniapp.service.MiniAppAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/miniapp/auth")
public class MiniAppAuthController {

    @Autowired
    private MiniAppAuthService miniAppAuthService;

    /**
     * 微信小程序登录接口
     * @param code 微信登录code
     * @param encryptedData 加密的用户数据
     * @param iv 加密算法的初始向量
     * @return 登录结果
     */
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestParam String code,
                             @RequestParam(required = false) String encryptedData,
                             @RequestParam(required = false) String iv) {
        return miniAppAuthService.login(code, encryptedData, iv);
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
