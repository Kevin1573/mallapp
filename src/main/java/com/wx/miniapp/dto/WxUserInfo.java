package com.wx.miniapp.dto;

import lombok.Data;

/**
 * 微信用户信息
 */
@Data
public class WxUserInfo {
    private String openId;
    private String nickName;
    private String gender;
    private String city;
    private String province;
    private String country;
    private String avatarUrl;
    private String unionId;
    private Watermark watermark;

    @Data
    public static class Watermark {
        private String appid;
        private Long timestamp;
    }
}
