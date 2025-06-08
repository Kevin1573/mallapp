package com.wx.common.util;

import com.wechat.pay.java.core.cipher.AeadAesCipher;
import com.wechat.pay.java.core.cipher.AeadCipher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

/**
 * 微信支付密文工具类
 */
@Slf4j
public class WxPayCipherUtil {

    /**
     * 解密回调中的加密数据
     *
     * @param apiV3Key 微信支付 API V3 密钥
     * @param associatedData 附加数据
     * @param nonce 随机串
     * @param ciphertext 密文
     * @return 解密后的明文
     */
    public static String decryptToString(String apiV3Key, String associatedData, String nonce, String ciphertext) {
        try {
            AeadCipher aeadCipher = new AeadAesCipher(apiV3Key.getBytes());
            return new String(
                aeadCipher.decrypt(
                    Base64.decodeBase64(ciphertext),
                    associatedData.getBytes(),
                    nonce.getBytes()
                )
            );
        } catch (Exception e) {
            log.error("解密微信支付回调数据失败", e);
            throw new RuntimeException("解密失败: " + e.getMessage());
        }
    }
}
