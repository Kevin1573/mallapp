package com.wx.controller;

import com.wx.service.AlipayService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alipay")
public class AlipayController {
    
    @GetMapping("/pay")
    @ResponseBody
    public String pay() {
        // 生成商户订单号
        String outTradeNo = "ORDER_" + System.currentTimeMillis();
        // 支付金额
        String totalAmount = "0.01";
        // 订单标题
        String subject = "测试商品";
        
        // 调用支付
        return AlipayService.pagePay(outTradeNo, totalAmount, subject);
    }
    
    // 支付成功同步回调
    @GetMapping("/return")
    public String returnUrl() {
        // 处理支付成功后的逻辑
        return "redirect:/success.html";
    }
    
    // 支付成功异步回调
    @RequestMapping("/notify")
    @ResponseBody
    public String notifyUrl(HttpServletRequest request) {
        // 验证签名和处理业务逻辑
        return "success";
    }
}