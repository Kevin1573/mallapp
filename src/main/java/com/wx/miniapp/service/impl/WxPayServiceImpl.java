package com.wx.miniapp.service.impl;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wx.miniapp.service.PayService;
import com.wx.miniapp.util.WxPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WxPayServiceImpl implements PayService {

    @Autowired
    private Config  wechatPayConfig;

    @Autowired
    private WxPayUtil wxPayUtil;

    @Value("${wx.miniapp-pay.appId}")
    private String appId;

    @Value("${wx.miniapp-pay.mchId}")
    private String mchId;

    @Value("${wx.miniapp-pay.notifyUrl}")
    private String notifyUrl;

    @Override
    public boolean verifySignature(Map<String, String> params) {
        // APIv3回调使用WechatPay-Signature头验证
        // 实际验证在Controller中通过SDK自动完成
        return true;
    }

    @Override
    public boolean processPayment(String orderNo, String transactionId, int totalFee) {
        // 查询订单确认支付状态
        JsapiService service = new JsapiService.Builder().config(wechatPayConfig).build();
        Transaction transaction = service.queryOrderByOutTradeNo(new QueryOrderByOutTradeNoRequest() {{
            setMchid(mchId);
            setOutTradeNo(orderNo);
        }});
        if (Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
            // TODO: 实现订单状态更新等业务逻辑
            return true;
        }
        return false;
    }

    @Override
    public Map<String, String> createJsapiPayment(String openid, String orderNo, int amount, String description) {
        JsapiService service = new JsapiService.Builder().config(wechatPayConfig).build();

        PrepayRequest request = new PrepayRequest();
        Amount amountObj = new Amount();
        amountObj.setTotal(amount);
        request.setAmount(amountObj);
        request.setAppid(appId);
        request.setMchid(mchId);
        request.setDescription(description);
        request.setNotifyUrl(notifyUrl);
        request.setOutTradeNo(orderNo);
        Payer payer = new Payer();
        payer.setOpenid(openid);
        request.setPayer(payer);

        PrepayResponse response = service.prepay(request);

        Map<String, String> result = new HashMap<>();
        result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("nonceStr", wxPayUtil.generateNonceStr());
        result.put("package", "prepay_id=" + response.getPrepayId());
        result.put("signType", "RSA");
        result.put("paySign", wxPayUtil.generateSignature(result));
        return result;
    }
}
