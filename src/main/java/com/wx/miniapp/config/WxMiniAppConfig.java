package com.wx.miniapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMiniAppConfig {
    // Getters and Setters
    private String appId;
    private String appSecret;
    private String tokenExpireTime;

}
