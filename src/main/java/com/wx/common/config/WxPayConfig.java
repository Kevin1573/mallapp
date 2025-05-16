package com.wx.common.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wx.common.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wx.common.utils.Constants.*;

@Configuration
public class WxPayConfig {

    @Bean
    public Config payConfig() {
        return new RSAPublicKeyConfig.Builder()
                .merchantId(MERCHANT_ID)
                .privateKeyFromPath(Constants.PRIVATE_KEY_PATH)
                .merchantSerialNumber(MERCHANT_SERIAL_NUMBER)
                .publicKey(Constants.PUBLIC_KEY_PATH)
                .publicKeyId(Constants.PUBLIC_KEY_ID)
                .apiV3Key(API_V3_KEY)
                .build();
    }

    @Bean
    public Config config() {
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(MERCHANT_ID)
                .merchantSerialNumber(MERCHANT_SERIAL_NUMBER)
                .apiV3Key(API_V3_KEY)
                .privateKey("68EE48914BD37392C6E2320FD07DAD71FD61F69F")
                .build();
    }

    @Bean
    public NativePayService nativePayService(Config config) {
        return new NativePayService.Builder().config(config).build();
    }

}
