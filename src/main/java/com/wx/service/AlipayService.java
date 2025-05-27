package com.wx.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.wx.common.config.AlipayConfig4253;
import com.wx.common.config.AlipayConfigConf;

public class AlipayService {
    
    /**
     * 电脑网站支付
     * @param outTradeNo 商户订单号
     * @param totalAmount 支付金额
     * @param subject 订单标题
     * @return 支付页面表单
     */
    public static String pagePay(String outTradeNo, String totalAmount, String subject) {
        AlipayClient alipayClient = AlipayConfigConf.alipayClient;
        
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(AlipayConfigConf.RETURN_URL);
        request.setNotifyUrl(AlipayConfigConf.NOTIFY_URL);
        
        // 创建API对应的请求参数
        String bizContent = "{" +
                "    \"out_trade_no\":\"" + outTradeNo + "\"," +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "    \"total_amount\":" + totalAmount + "," +
                "    \"subject\":\"" + subject + "\"," +
                "    \"body\":\"商品描述\"," +
                "    \"timeout_express\":\"90m\"" +
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

    /**
     * 创建支付宝订单并返回支付二维码
     * @param totalAmount 支付金额（单位：元）
     * @param subject 订单标题
     * @param outTradeNo 商户订单号
     * @return 支付二维码URL
     */
    public static String createQrPayment(String outTradeNo, String totalAmount, String subject) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig4253.getAlipayConfigX(AlipayConfig4253.APP_PRIVATE_KEY,
                        AlipayConfig4253.ALIPAY_PUBLIC_KEY, AlipayConfig4253.APP_ID));
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();

        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setOutTradeNo(outTradeNo);
        model.setTotalAmount(totalAmount);
        model.setSubject(subject);
        // model.setProductCode("FAST_INSTANT_TRADE_PAY"); // 产品码改为线上交易
        model.setProductCode("QR_CODE_OFFLINE");
        model.setTimeoutExpress("90m");
        model.setQrCodeTimeoutExpress("90m");
        request.setReturnUrl(AlipayConfigConf.RETURN_URL);
        request.setNotifyUrl(AlipayConfigConf.NOTIFY_URL);

        request.setBizModel(model);
        request.setNotifyUrl(AlipayConfigConf.NOTIFY_URL); // 异步通知地址

        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        if (!response.isSuccess()) {
            throw new RuntimeException("支付宝接口调用失败: " + response.getSubMsg());
        }
        return response.getQrCode(); // 直接返回二维码内容
    }
}