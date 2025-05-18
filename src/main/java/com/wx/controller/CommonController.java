package com.wx.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/common")
public class CommonController {

    // 添加heath  接口
    @RequestMapping(value = "/health")
    public String health() {
        return "ok";
    }
}
