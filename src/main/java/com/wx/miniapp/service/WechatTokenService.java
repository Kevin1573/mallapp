package com.wx.miniapp.service;

import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wx.common.utils.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WechatTokenService {
    @Value("${wx.miniapp.app-id}")
    private String appid;

    @Value("${wx.miniapp.app-secret}")
    private String secret;

    private final RestTemplate restTemplate = new RestTemplate();

    @SneakyThrows
    public String getAccessToken() {
        String url = String.format(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                appid, secret);
        System.out.println(url);
        String response = HttpUtil.get(url);
        System.out.println(response);

        if (response != null && !response.isEmpty()) {
            WechatTokenResponse wechatTokenResponse = JsonUtil.parseObj(response, WechatTokenResponse.class);
            return wechatTokenResponse.getAccessToken();
        }
        throw new RuntimeException("获取微信access_token失败");
    }

    public static void main(String[] args) {
//        String response = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx3681af7917801822&secret=47b04397685a3974cae9076a3e8ce6af");
//        System.out.println(response);

        String jsonStr = "{\"access_token\":\"93_JsWnxctLc3DqjrdslQB1XyVOBZWu_f0Qk6qfdPaNGKcqXPcUCE4nM6aqRc4QGRAmchPww0Agc0-0l_PG74biKZaEvyRi5KxLTIPhXZvrG2RLNWXKCbePNrqIrdMRIUhAHABSE\",\"expires_in\":7200}";

        try {
            WechatTokenResponse tokenInfo = JsonUtil.parseObj(jsonStr, WechatTokenResponse.class);
            System.out.println(tokenInfo.getAccessToken());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 返回数据结构
    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WechatTokenResponse {
        // 可选setter方法
        // 必须提供getter方法
        @JsonProperty("access_token")  // 明确映射JSON属性
        private String accessToken;

        @JsonProperty("expires_in")
        private int expiresIn;

    }
}
