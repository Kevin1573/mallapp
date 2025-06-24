package com.wx.miniapp.util;

//import com.wechat.pay.java.core.util.Signer;

import com.wechat.pay.java.core.cipher.Signer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class WxPayUtil {

    @Autowired
    private Signer signer;

    /**
     * 生成随机字符串
     */
    public  String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    /**
     * 生成支付签名
     */
    public  String generateSignature(Map<String, String> params) {
//        String message = buildSignMessage(params);
        String message = String.format("%s\n%s\n%s\n%s\n",
                params.get("appId"), params.get("timeStamp"), params.get("nonceStr"), params.get("package") );
        System.out.println("generate signature: "+message);
        return signer.sign(message).getSign();
    }

    /**
     * 构建待签名字符串
     */
    private  String buildSignMessage(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&"));
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 生成成功响应
     */
    public static String generateSuccessResponse() {
        return "{\"code\": \"SUCCESS\", \"message\": \"OK\"}";
    }

    /**
     * 生成错误响应
     */
    public static String generateErrorResponse(String errorMsg) {
        return String.format("{\"code\": \"FAIL\", \"message\": \"%s\"}", errorMsg);
    }
}
