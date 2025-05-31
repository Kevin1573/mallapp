package com.wx.common.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RSAPublicKeyNotificationConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Component
@ConfigurationProperties(prefix = "wx.pay")
@Data
public class MultiMerchantWxPayConfig {

    private Map<String, MerchantConfig> merchants;

    @Data
    public static class MerchantConfig {
        private String merchantId; // 商户号
        private String appId; // 应用ID
        private String privateKeyPath; // 商户私钥路径
        private String publicKeyPath; // 微信支付公钥路径
        private String publicKeyId; // 微信支付公钥ID
        private String merchantSerialNumber; // 商户证书序列号
        private String apiV3Key; // API v3密钥
        private String notifyUrl; // 支付回调地址
    }

    private final Map<String, Config> configCache = new ConcurrentHashMap<>();
    private final Map<String, NotificationParser> parserCache = new ConcurrentHashMap<>();
    private final Map<String, NativePayService> serviceCache = new ConcurrentHashMap<>();

    public Config getPayConfig(String merchantId) {
        return configCache.computeIfAbsent(merchantId, key -> {
            MerchantConfig merchantConfig = merchants.get(key);
            if (merchantConfig == null) {
                throw new IllegalArgumentException("商户配置不存在: " + key);
            }

            return new RSAPublicKeyConfig.Builder()
                    .merchantId(merchantConfig.getMerchantId())
                    .privateKeyFromPath(merchantConfig.getPrivateKeyPath())
                    .publicKeyFromPath(merchantConfig.getPublicKeyPath())
                    .publicKeyId(merchantConfig.getPublicKeyId())
                    .merchantSerialNumber(merchantConfig.getMerchantSerialNumber())
                    .apiV3Key(merchantConfig.getApiV3Key())
                    .build();
        });
    }

    public NotificationParser getNotificationParser(String merchantId) {
        return parserCache.computeIfAbsent(merchantId, key -> {
            MerchantConfig merchantConfig = merchants.get(key);
            if (merchantConfig == null) {
                throw new IllegalArgumentException("商户配置不存在: " + key);
            }

            NotificationConfig notificationConfig = new RSAPublicKeyNotificationConfig.Builder()
                    .publicKeyFromPath(merchantConfig.getPublicKeyPath())
                    .publicKeyId(merchantConfig.getPublicKeyId())
                    .apiV3Key(merchantConfig.getApiV3Key())
                    .build();

            return new NotificationParser(notificationConfig);
        });
    }

    public NativePayService getNativePayService(String merchantId) {
        return serviceCache.computeIfAbsent(merchantId, key -> {
            Config config = getPayConfig(key);
            return new NativePayService.Builder().config(config).build();
        });
    }

    public MerchantConfig getMerchantConfig(String merchantId) {
        if (merchants == null) {
            throw new IllegalStateException("商户配置未初始化，请检查是否已正确配置wx.pay.merchants属性");
        }
        MerchantConfig config = merchants.get(merchantId);
        if (config == null) {
            throw new IllegalArgumentException("商户配置不存在: " + merchantId);
        }
        return config;
    }
}
