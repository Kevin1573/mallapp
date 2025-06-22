package com.wx.miniapp.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

//@Configuration
public class WechatPayConfig {

    @Value("${wx.pay.mchid}")
    private String mchId;

    @Value("${wx.pay.mchSerialNo}")
    private String mchSerialNo;

    @Value("${wx.pay.apiV3Key}")
    private String apiV3Key;

    @Value("${wx.pay.privateKey}")
    private String privateKey;

    @Bean
    public Config wechatPayConfig() {
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(mchId)
                .privateKey(privateKey)
                .merchantSerialNumber(mchSerialNo)
                .apiV3Key(apiV3Key)
                .build();
    }
}
