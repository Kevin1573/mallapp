package com.wx.miniapp.service;

import java.util.Map;

public interface PayService {
    /**
     * 创建JSAPI支付
     * @param openid 用户openid
     * @param orderNo 商户订单号
     * @param amount 金额(分)
     * @param description 商品描述
     * @return 小程序支付所需参数
     */
    Map<String, String> createJsapiPayment(String token, String orderNo, String from);

    /**
     * 验证支付回调签名
     */
    boolean verifySignature(Map<String, String> params);

    /**
     * 处理支付结果
     */
    boolean processPayment(String orderNo, String transactionId, int totalFee);
}
