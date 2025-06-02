package com.wx.common.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RSAPublicKeyNotificationConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.refund.RefundService;
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

    private Map<String, MerchantConfig> merchants = new ConcurrentHashMap<>();



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
        private String refundNotifyUrl;   // 退款结果通知地址
        private boolean sandboxMode = false; // 是否沙箱模式
    }

    // 配置缓存，避免重复创建
    private final Map<String, Config> configCache = new ConcurrentHashMap<>();
    private final Map<String, NotificationParser> parserCache = new ConcurrentHashMap<>();
    // 支付服务缓存
    private final Map<String, NativePayService> payServiceCache = new ConcurrentHashMap<>();
    // 退款服务缓存
    private final Map<String, RefundService> refundServiceCache = new ConcurrentHashMap<>();

    /**
     * 获取商户配置
     */
    public MerchantConfig getMerchantConfig(String merchantId) {
        MerchantConfig config = merchants.get(merchantId);
        if (config == null) {
            throw new IllegalArgumentException("商户配置不存在: " + merchantId);
        }
        return config;
    }

    /**
     * 获取微信支付配置
     */
    public Config getPayConfig(String merchantId) {
        return configCache.computeIfAbsent(merchantId, key -> {
            MerchantConfig merchantConfig = getMerchantConfig(key);
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
            MerchantConfig merchantConfig = getMerchantConfig(merchantId);
            NotificationConfig notificationConfig = new RSAPublicKeyNotificationConfig.Builder()
                    .publicKeyFromPath(merchantConfig.getPublicKeyPath())
                    .publicKeyId(merchantConfig.getPublicKeyId())
                    .apiV3Key(merchantConfig.getApiV3Key())
                    .build();

            return new NotificationParser(notificationConfig);
        });
    }

    /**
     * 获取Native支付服务
     */
    public NativePayService getNativePayService(String merchantId) {
        return payServiceCache.computeIfAbsent(merchantId, key -> {
            Config config = getPayConfig(key);
            return new NativePayService.Builder().config(config).build();
        });
    }

    /**
     * 获取退款服务
     */
    public RefundService getRefundService(String merchantId) {
        return refundServiceCache.computeIfAbsent(merchantId, key -> {
            Config config = getPayConfig(key);
            return new RefundService.Builder().config(config).build();
        });
    }

    /**
     * 清除指定商户的缓存
     */
    public void clearCache(String merchantId) {
        configCache.remove(merchantId);
        payServiceCache.remove(merchantId);
        refundServiceCache.remove(merchantId);
    }

    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        configCache.clear();
        payServiceCache.clear();
        refundServiceCache.clear();
    }

    /**
     * 验证商户配置是否完整
     */
    public void validateMerchantConfig(String merchantId) {
        MerchantConfig config = getMerchantConfig(merchantId);

        if (config.getMerchantId() == null || config.getMerchantId().trim().isEmpty()) {
            throw new IllegalArgumentException("商户 " + merchantId + " 的 merchantId 不能为空");
        }
        if (config.getMerchantSerialNumber() == null || config.getMerchantSerialNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("商户 " + merchantId + " 的 merchantSerialNumber 不能为空");
        }
        if (config.getPrivateKeyPath() == null || config.getPrivateKeyPath().trim().isEmpty()) {
            throw new IllegalArgumentException("商户 " + merchantId + " 的 privateKeyPath 不能为空");
        }
        if (config.getApiV3Key() == null || config.getApiV3Key().trim().isEmpty()) {
            throw new IllegalArgumentException("商户 " + merchantId + " 的 apiV3Key 不能为空");
        }
        if (config.getNotifyUrl() == null || config.getNotifyUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("商户 " + merchantId + " 的 notifyUrl 不能为空");
        }
        if (config.getRefundNotifyUrl() == null || config.getRefundNotifyUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("商户 " + merchantId + " 的 refundNotifyUrl 不能为空");
        }
    }

    /**
     * 检查并更新商户配置
     * 如果配置发生变化，会清除对应的缓存
     */
    public void updateMerchantConfig(String merchantId, MerchantConfig newConfig) {
        MerchantConfig oldConfig = merchants.get(merchantId);
        if (oldConfig != null && !oldConfig.equals(newConfig)) {
            clearCache(merchantId);
        }
        merchants.put(merchantId, newConfig);
        validateMerchantConfig(merchantId);
    }
}
