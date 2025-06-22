package com.wx.miniapp.service;

import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wx.common.utils.JsonUtil;
import com.wx.miniapp.util.WxMiniAppUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class WechatInfoService {

    @Value("${wx.miniapp.app-id}")
    private String appid;
    @Autowired
    private WechatTokenService tokenService;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getPhoneNumber(String code) {
        try {
            // 1. 获取access_token
            String accessToken = tokenService.getAccessToken();
            System.out.println("accessToken: " + accessToken);

            // 2. 构造请求URL
            String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken;
            String post = HttpUtil.post(url, "{\"code\":\"" + code + "\"}");
            System.out.println("post: " + post);
            // 3. 解析响应
            WechatPhoneResponse response = JsonUtil.parseObj(post, WechatPhoneResponse.class);
            if (response.getErrcode() == 0) {
                return response.getPhone_info().getPhoneNumber();
            } else {
                return "服务器错误: " + response.getErrmsg();
            }

        } catch (Exception e) {
            return "服务器错误: " + e.getMessage();
        }
    }

    public static void main(String[] args) {

        String json = "{\"errcode\":0,\"errmsg\":\"ok\",\"phone_info\":{\"phoneNumber\":\"18191757040\",\"purePhoneNumber\":\"18191757040\",\"countryCode\":\"86\",\"watermark\":{\"timestamp\":1750622184,\"appid\":\"wxedc2e7e07b364ea6\"}}}";
        WechatPhoneResponse response = JsonUtil.parseObj(json, WechatPhoneResponse.class);
        System.out.println(response);
    }

    public static void main2(String[] args) {
//        String response = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx3681af7917801822&secret=47b04397685a3974cae9076a3e8ce6af");
        String response = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxedc2e7e07b364ea6&secret=1599615e74cc8d3e595ed13fa9df7266");
        WechatTokenService.WechatTokenResponse tokenInfo = JsonUtil.parseObj(response, WechatTokenService.WechatTokenResponse.class);
        System.out.println(tokenInfo.getAccessToken());
        String code = "c2d1fe6126f3c6607c184f5fb3bd46f2993f8f9125dabdc01681699791ce5e27";
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + tokenInfo.getAccessToken();
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", code);
//        map.put("openid", "oua2r7fL0g5w79eyUQ0qcYIR-wsQ");

                    Map<String, String> authInfo = WxMiniAppUtil.code2Session(
                    "wxedc2e7e07b364ea6",
                    "1599615e74cc8d3e595ed13fa9df7266",
                    code);

        String openid = authInfo.get("openid");

        String post = HttpUtil.post(url, "{\n" +
                "\t\"code\": \""+code+"\"" +
                "}");
        System.out.println("post: " + post);
    }


    // 微信响应结构
//    @Data
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class WechatPhoneResponse {
//        // getters
//        @JsonProperty("errcode")
//        private int errcode;
//        @JsonProperty("errcode")
//        private String errmsg;
//        @JsonProperty("phone_info")
//        private PhoneInfo phoneInfo;
//
//    }
//
//    // 手机号信息结构
//    @Data
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class PhoneInfo {
//        @JsonProperty("phoneNumber")
//        private String phoneNumber;
//        private String purePhoneNumber;
//        private String countryCode;
//    }

//    @Data
//    public class WechatPhoneResponse {
//        @JsonProperty("errcode")
//        private int errCode;
//
//        @JsonProperty("errmsg")
//        private String errMsg;
//
//        @JsonProperty("phone_info")
//        private PhoneInfo phoneInfo;
//    }
//
//    // 手机号信息类
//    @Data
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class PhoneInfo {
//        @JsonProperty("phoneNumber")
//        private String phoneNumber;
//
//        @JsonProperty("purePhoneNumber")
//        private String purePhoneNumber;
//
//        @JsonProperty("countryCode")
//        private String countryCode;
//
//        // getters/setters
//
//    }

    // 静态内部类+无参构造+Lombok
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WechatPhoneResponse {
        private int errcode;
        private String errmsg;
        private PhoneInfo phone_info;
    }

    @Data
    @NoArgsConstructor
    public static class PhoneInfo {
        private String phoneNumber;
        private String purePhoneNumber;
        private String countryCode;
    }
}
