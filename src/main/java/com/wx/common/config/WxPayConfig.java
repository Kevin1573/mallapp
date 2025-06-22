package com.wx.common.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.core.cipher.RSASigner;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RSAPublicKeyNotificationConfig;
import com.wechat.pay.java.core.util.PemUtil;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wx.common.config.PayConstants.*;

@Configuration
public class WxPayConfig {

    @Value("${spring.profiles.active}")
    private String profile;

    @Bean
    public Config payConfig() {

        return new RSAPublicKeyConfig.Builder()
                .merchantId(MERCHANT_ID) //微信支付的商户号
                .privateKeyFromPath("dev".equals(profile) ? PRIVATE_KEY_PATH_LOCAL : PRIVATE_KEY_PATH) // 商户API证书私钥的存放路径
                .publicKeyFromPath("dev".equals(profile) ? LOCAL_PUBLIC_KEY_PATH : PUBLIC_KEY_PATH) //微信支付公钥的存放路径
                .publicKeyId(PUBLIC_KEY_ID) //微信支付公钥ID
                .merchantSerialNumber(MERCHANT_SERIAL_NUMBER) //商户API证书序列号
                .apiV3Key(API_V3_KEY) //APIv3密钥
                .build();
    }

    @Bean
    public NotificationParser createParser() {
        NotificationConfig notificationConfig = new RSAPublicKeyNotificationConfig.Builder()
                .publicKeyFromPath("dev".equals(profile) ? LOCAL_PUBLIC_KEY_PATH : PUBLIC_KEY_PATH)
                .publicKeyId(PUBLIC_KEY_ID)
                .apiV3Key(API_V3_KEY)
                .build();
        // 2. 初始化通知解析器
        return new NotificationParser(notificationConfig);
    }

    @Bean
    public NativePayService nativePayService(Config config) {
        return new NativePayService.Builder().config(config).build();
    }

    @Bean
    public JsapiService jsapiService(Config config) {
        return new JsapiService.Builder().config(config).build();
    }

    @Bean
    public RSASigner createSigner() {
        return new RSASigner(
                MERCHANT_ID,
                PemUtil.loadPrivateKeyFromPath("dev".equals(profile) ?
                        PRIVATE_KEY_PATH_LOCAL : PRIVATE_KEY_PATH)
        );
    }

}
