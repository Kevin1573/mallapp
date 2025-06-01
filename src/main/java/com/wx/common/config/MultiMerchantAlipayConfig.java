package com.wx.common.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class MultiMerchantAlipayConfig {

    private Map<String, MerchantConfig> merchants = new ConcurrentHashMap<>();

    @Data
    public static class MerchantConfig {
        private String appId;           // 应用ID
        private String privateKey;      // 商户私钥
        private String publicKey;       // 支付宝公钥
        private String notifyUrl;       // 支付异步通知地址
        private String returnUrl;       // 支付同步返回地址
        private String signType = "RSA2"; // 签名方式
        private String charset = "UTF-8"; // 字符编码格式
        private String format = "json";   // 返回格式
        private String serverUrl = "https://openapi.alipay.com/gateway.do"; // 支付宝网关
        private boolean sandboxMode = false; // 是否沙箱模式
    }

    // 客户端缓存，避免重复创建
    private final Map<String, AlipayClient> clientCache = new ConcurrentHashMap<>();

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
     * 获取支付宝客户端
     */
    public AlipayClient getAlipayClient(String merchantId) throws AlipayApiException {
        return clientCache.computeIfAbsent(merchantId, key -> {
            MerchantConfig config = getMerchantConfig(key);
            String serverUrl = config.isSandboxMode() ?
                    "https://openapi-sandbox.dl.alipaydev.com/gateway.do" : // 沙箱环境
                    config.getServerUrl(); // 正式环境

            return new DefaultAlipayClient(
                    serverUrl,
                    config.getAppId(),
                    config.getPrivateKey(),
                    config.getFormat(),
                    config.getCharset(),
                    config.getPublicKey(),
                    config.getSignType()
            );
        });
    }

    /**
     * 清除指定商户的客户端缓存
     */
    public void clearClientCache(String merchantId) {
        clientCache.remove(merchantId);
    }

    /**
     * 清除所有商户的客户端缓存
     */
    public void clearAllClientCache() {
        clientCache.clear();
    }

    /**
     * 验证商户配置是否完整
     */
    public void validateMerchantConfig(String merchantId) {
        MerchantConfig config = getMerchantConfig(merchantId);

        if (config.getAppId() == null || config.getAppId().trim().isEmpty()) {
            throw new IllegalArgumentException("商户 " + merchantId + " 的 appId 不能为空");
        }
        if (config.getPrivateKey() == null || config.getPrivateKey().trim().isEmpty()) {
            throw new IllegalArgumentException("商户 " + merchantId + " 的 privateKey 不能为空");
        }
        if (config.getPublicKey() == null || config.getPublicKey().trim().isEmpty()) {
            throw new IllegalArgumentException("商户 " + merchantId + " 的 publicKey 不能为空");
        }
        if (config.getNotifyUrl() == null || config.getNotifyUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("商户 " + merchantId + " 的 notifyUrl 不能为空");
        }
    }

    /**
     * 检查并更新商户配置
     * 如果配置发生变化，会清除对应的客户端缓存
     */
    public void updateMerchantConfig(String merchantId, MerchantConfig newConfig) {
        MerchantConfig oldConfig = merchants.get(merchantId);
        if (oldConfig != null && !oldConfig.equals(newConfig)) {
            clearClientCache(merchantId);
        }
        merchants.put(merchantId, newConfig);
        validateMerchantConfig(merchantId);
    }
}
