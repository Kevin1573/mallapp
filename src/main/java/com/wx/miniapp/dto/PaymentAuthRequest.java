package com.wx.miniapp.dto;

import lombok.Data;

@Data
public class PaymentAuthRequest {
    // 微信登录code
    private String code;
    // 加密的用户数据
    private String encryptedData;
    // 加密算法的初始向量
    private String iv;

}
