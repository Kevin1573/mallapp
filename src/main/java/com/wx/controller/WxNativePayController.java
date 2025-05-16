package com.wx.controller;

import com.wx.service.WxNativePayService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wxpay/native")
public class WxNativePayController {

    private final WxNativePayService wxNativePayService;

    public WxNativePayController(WxNativePayService wxNativePayService) {
        this.wxNativePayService = wxNativePayService;
    }

    @PostMapping("/create")
    public String createOrder(
            @RequestParam String outTradeNo,
            @RequestParam int amount,
            @RequestParam String description) {
        return wxNativePayService.createNativeOrder(outTradeNo, amount, description);
    }
}