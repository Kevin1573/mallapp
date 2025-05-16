package com.wx.common.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
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
    public NotificationParser notificationParser() {
        // 自动获取平台证书的配置
        Config config = new RSAAutoCertificateConfig.Builder()
                .merchantId(Constants.MERCHANT_ID)
                .merchantSerialNumber(Constants.MERCHANT_SERIAL_NUMBER)
                .apiV3Key(Constants.API_V3_KEY)
                .privateKey(privateKey)
                .build();

        return new NotificationParser(config);
    }

    @Bean
    public NativePayService payService(Config payConfig) {
        // 构建service
        return new NativePayService.Builder().config(payConfig).build();
    }

}
