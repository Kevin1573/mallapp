//package com.wx.common.config;
//
//import com.wx.common.utils.Constants;
//import com.wechat.pay.java.core.Config;
//import com.wechat.pay.java.core.RSAAutoCertificateConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class WxPayConfig {
//
//    @Bean
//    public Config createConfig () {
//        Config config =
//                new RSAAutoCertificateConfig.Builder()
//                        .merchantId(Constants.MERCHANT_ID)
//                        .privateKeyFromPath(Constants.PRIVATE_KEY_PATH)
//                        .merchantSerialNumber(Constants.MERCHANT_SERIAL_NUMBER)
//                        .apiV3Key(Constants.API_V3_KEY)
//                        .build();
//        return config;
//    }
//}
