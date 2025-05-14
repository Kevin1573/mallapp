package com.wx.common.config;

import com.wx.common.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfig {

    public com.aliyun.dysmsapi20170525.Client createSmsClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(Constants.ACCESS_KEY_ID)
                .setAccessKeySecret(Constants.ACCESS_KEY_SECRET);
        // 访问的域名
        config.endpoint = Constants.ENDPOINT;
        return new com.aliyun.dysmsapi20170525.Client(config);
    }
}
