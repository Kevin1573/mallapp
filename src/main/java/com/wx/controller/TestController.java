package com.wx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @GetMapping("/wechat/oauth")
    public String oauth(@RequestParam("code") String code) throws JsonProcessingException {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=YOUR_APPID" +
                "&secret=YOUR_SECRET" +
                "&code=" + code +
                "&grant_type=authorization_code";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        String openId = jsonNode.get("openid").asText();

        return "OpenID: " + openId;
    }
}
