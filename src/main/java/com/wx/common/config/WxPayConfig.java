package com.wx.common.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wx.common.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WxPayConfig {

    @Bean
    public Config payConfig() {
        return new RSAPublicKeyConfig.Builder()
                .merchantId(Constants.MERCHANT_ID)
                .privateKeyFromPath(Constants.PRIVATE_KEY_PATH)
                .merchantSerialNumber(Constants.MERCHANT_SERIAL_NUMBER)
                .publicKey(Constants.PUBLIC_KEY_PATH)
                .publicKeyId(Constants.PUBLIC_KEY_ID)
                .apiV3Key(Constants.API_V3_KEY)
                .build();
    }

    @Bean
    public NativePayService payService(Config payConfig) {
        // 构建service
        return new NativePayService.Builder().config(payConfig).build();
    }

}
