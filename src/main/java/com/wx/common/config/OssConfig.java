package com.wx.common.config;

import com.aliyun.sts20150401.Client;
import com.aliyun.teaopenapi.models.Config;
import com.wx.common.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    @Bean
    public Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(Constants.ACCESS_KEY_ID)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(Constants.ACCESS_KEY_SECRET);
        config.endpoint = Constants.ENDPOINT;
        return new Client(config);
    }
}
