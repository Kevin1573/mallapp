package com.wx.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.wx.common.config.AlipayConfig;

public class AlipayService {
    
    /**
     * 电脑网站支付
     * @param outTradeNo 商户订单号
     * @param totalAmount 支付金额
     * @param subject 订单标题
     * @return 支付页面表单
     */
    public static String pagePay(String outTradeNo, String totalAmount, String subject) {
        AlipayClient alipayClient = AlipayConfig.alipayClient;
        
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(AlipayConfig.RETURN_URL);
        request.setNotifyUrl(AlipayConfig.NOTIFY_URL);
        
        // 创建API对应的请求参数
        String bizContent = "{" +
                "    \"out_trade_no\":\"" + outTradeNo + "\"," +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "    \"total_amount\":" + totalAmount + "," +
                "    \"subject\":\"" + subject + "\"," +
                "    \"body\":\"商品描述\"," +
                "    \"notify_url\":\"http://g8a65b28.natappfree.cc/alipay/notify\"" +
                "  }";
        
        request.setBizContent(bizContent);
        
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                return response.getBody();
            } else {
                throw new RuntimeException("支付宝支付请求失败: " + response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException("支付宝支付异常", e);
        }
    }
}