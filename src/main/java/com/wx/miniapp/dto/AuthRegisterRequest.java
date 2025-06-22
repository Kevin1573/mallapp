package com.wx.miniapp.dto;

import lombok.Data;

@Data
public class AuthRegisterRequest {
    private String token;
    private String code;
    private String fromShop;

}
