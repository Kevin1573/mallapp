package com.wx.common.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wx.common.config.PayConstants.*;

@Configuration
public class WxPayConfig {

    @Bean
    public Config payConfig() {
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(MERCHANT_ID)
                .privateKeyFromPath(PRIVATE_KEY_PATH)
                .merchantSerialNumber(MERCHANT_SERIAL_NUMBER)
                .apiV3Key(API_V3_KEY)
                .build();
    }

    @Bean
    public NotificationParser createParser() {
        NotificationConfig notificationConfig = new RSAAutoCertificateConfig.Builder()
                .apiV3Key(API_V3_KEY)
                .privateKeyFromPath(PRIVATE_KEY_PATH)
                .merchantId(MERCHANT_ID)
                .merchantSerialNumber(MERCHANT_SERIAL_NUMBER)
                .build();
        // 2. 初始化通知解析器
        return new NotificationParser(notificationConfig);
    }

    @Bean
    public NativePayService nativePayService(Config config) {
        return new NativePayService.Builder().config(config).build();
    }

}
