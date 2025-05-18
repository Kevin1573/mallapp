package com.wx.service;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wx.orm.entity.PaymentConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class PaymentServiceFactory {
    private final ConcurrentHashMap<String, Object> serviceCache = new ConcurrentHashMap<>();
    private final PaymentConfigService configService; // 改为使用Service

    @SuppressWarnings("unchecked")
    public <T> T getOrCreateService(String mchId, String channel) {
        String cacheKey = buildCacheKey(mchId, channel);
        return (T) serviceCache.computeIfAbsent(cacheKey, k -> {
            PaymentConfig config = configService.getActiveConfig(mchId, channel);
            if (config == null) {
                throw new RuntimeException("未找到有效支付配置");
            }
            return createService(config);
        });
    }

    private Object createService(PaymentConfig config) {
        switch (config.getPayChannel().toUpperCase()) {
            case "WXPAY":
                return buildWechatService(config);
            case "ALIPAY":
                return buildAlipayService(config);
            default:
                throw new IllegalArgumentException("不支持的支付渠道");
        }
    }

    private NativePayService buildWechatService(PaymentConfig config) {
        RSAAutoCertificateConfig rsaConfig = new RSAAutoCertificateConfig.Builder()
                .merchantId(config.getMchId())
                .privateKeyFromPath(config.getWxPrivateKeyPath())
                .merchantSerialNumber(config.getWxMchSerialNo())
                .apiV3Key(config.getApiV3Key())
                .build();

        return new NativePayService.Builder().config(rsaConfig).build();
    }

    private NotificationParser buildWechatNotificationParser(PaymentConfig config) {
        RSAAutoCertificateConfig rsaConfig = new RSAAutoCertificateConfig.Builder()
                .merchantId(config.getMchId())
                .privateKeyFromPath(config.getWxPrivateKeyPath())
                .merchantSerialNumber(config.getWxMchSerialNo())
                .apiV3Key(config.getApiV3Key())
                .build();

        return new NotificationParser(rsaConfig);
    }

    private AlipayClient buildAlipayService(PaymentConfig config) {
        return new DefaultAlipayClient(
                config.getGatewayUrl(),
                config.getAppId(),
                config.getAppPrivateKey(),
                "json",
                "UTF-8",
                config.getAlipayPublicKey(),
                "RSA2");
    }

    private String buildCacheKey(String mchId, String channel) {
        return mchId + "::" + channel.toUpperCase();
    }
}

