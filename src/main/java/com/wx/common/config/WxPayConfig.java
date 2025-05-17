package com.wx.common.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WxPayConfig {

    @Bean
    public NativePayService nativePayService(Config config) {
        return new NativePayService.Builder().config(config).build();
    }

}
