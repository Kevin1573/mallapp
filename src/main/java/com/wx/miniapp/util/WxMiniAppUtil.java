package com.wx.miniapp.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wx.miniapp.dto.WxUserInfo;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

public class WxMiniAppUtil {

    private static final String CODE_TO_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * 使用code换取session_key和openid
     * @param appId 小程序appId
     * @param appSecret 小程序appSecret
     * @param code 登录code
     * @return 包含session_key和openid的map
     */
    public static Map<String, String> code2Session(String appId, String appSecret, String code) {
        String url = CODE_TO_SESSION_URL + "?appid=" + appId + "&secret=" + appSecret + "&js_code=" + code + "&grant_type=authorization_code";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JSONObject json = JSON.parseObject(response.getBody());

        Map<String, String> result = new HashMap<>();
        if (json != null) {
            result.put("openid", json.getString("openid"));
            result.put("session_key", json.getString("session_key"));
            result.put("unionid", json.getString("unionid"));
        }

        return result;
    }

    /**
     * 解密用户数据
     * @param encryptedData 加密的用户数据
     * @param sessionKey 会话密钥
     * @param iv 加密算法的初始向量
     * @return 解密后的用户信息
     */
    public static WxUserInfo decryptUserInfo(String encryptedData, String sessionKey, String iv) {
        try {
            // Base64解码
            byte[] encryptedDataBytes = Base64.decodeBase64(encryptedData);
            byte[] sessionKeyBytes = Base64.decodeBase64(sessionKey);
            byte[] ivBytes = Base64.decodeBase64(iv);

            // 初始化AES解密
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(sessionKeyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            // 执行解密
            byte[] decryptedBytes = cipher.doFinal(encryptedDataBytes);
            String decryptedData = new String(decryptedBytes, StandardCharsets.UTF_8);

            // 解析为WxUserInfo对象
            return JSON.parseObject(decryptedData, WxUserInfo.class);
        } catch (Exception e) {
            throw new RuntimeException("解密用户数据失败", e);
        }
    }

    /**
     * 验证用户数据签名
     * @param rawData 原始数据
     * @param signature 签名
     * @param sessionKey 会话密钥
     * @return 是否验证通过
     */
    public static boolean checkSignature(String rawData, String signature, String sessionKey) {
        try {
            String data = rawData + sessionKey;
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = byteToHex(digest);

            return calculatedSignature.equals(signature);
        } catch (Exception e) {
            throw new RuntimeException("验证签名失败", e);
        }
    }

    private static String byteToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
