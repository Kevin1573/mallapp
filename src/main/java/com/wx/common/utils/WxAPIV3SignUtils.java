package com.wx.common.utils;

//import com.wechat.pay.java.core.util.PemUtil;
import okhttp3.HttpUrl;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Random;

public class WxAPIV3SignUtils {
    // Authorization: <schema> <token>
    // GET - getToken("GET", httpurl, "")
    // POST - getToken("POST", httpurl, json)
    private static final String schema = "WECHATPAY2-SHA256-RSA2048";
    private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final Random RANDOM = new SecureRandom();

    /**
     * @param method
     * @param urlStr
     * @param body
     * @param mchId      商户号
     * @param privateKey 商户证书私钥
     * @param serialNo   商户API证书序列号
     * @return
     * @throws Exception
     */
    public static String getToken(String method, String urlStr, String body, String mchId, String privateKey, String serialNo) throws Exception {
        HttpUrl url = HttpUrl.parse(urlStr);
        String nonceStr = generateNonceStr();
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        PrivateKey privateKey1 = null;//PemUtil.loadPrivateKeyFromPath(Constants.PRIVATE_KEY_PATH);
        String signature = sign(message.getBytes(StandardCharsets.UTF_8), privateKey1);

        return schema + " " + "mchid=\"" + mchId + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + serialNo + "\","
                + "signature=\"" + signature + "\"";
    }

    /**
     * @param message
     * @param privateKey 商户私钥
     * @return
     */
    private static String sign(byte[] message, PrivateKey privateKey) throws Exception {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(message);

        return Base64.getEncoder().encodeToString(sign.sign());
    }

    private static String buildMessage(String method, HttpUrl url, long timestamp, String nonceStr, String body) {
        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null) {
            canonicalUrl += "?" + url.encodedQuery();
        }

        return method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }

    /**
     * String转私钥PrivateKey
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (Base64.getDecoder().decode(key));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 获取随机字符串 Nonce Str
     *
     * @return String 随机字符串
     */
    public static String generateNonceStr() {
        char[] nonceChars = new char[32];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }

}
