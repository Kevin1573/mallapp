package com.wx.controller;

import com.wx.common.utils.WeChatUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;

@RestController
public class OpenIdController {
    private static final String APP_ID = "wxe27f44eaa74ef46c";
    private static final String APP_SECRET = "b5e034741bb55248b9d7c4decf2e28a6";

    @PostMapping("/getOpenId")
    public String getOpenId(@RequestBody JSONObject request) throws Exception {
        String code = request.getString("code");
        String response = WeChatUtil.getOpenId(APP_ID, APP_SECRET, code);
        JSONObject result = JSONObject.parseObject(response);
        
        if (result.containsKey("openid")) {
            return result.getString("openid"); // 返回OpenID
        } else {
            throw new Exception("获取OpenID失败: " + result.getString("errmsg"));
        }
    }
}
