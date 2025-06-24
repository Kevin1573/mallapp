package com.wx.miniapp.service.impl;

import com.alibaba.fastjson.JSON;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wx.common.enums.PayWayEnums;
import com.wx.miniapp.service.PayService;
import com.wx.miniapp.util.WxPayUtil;
import com.wx.orm.entity.GoodsHistoryDO;
import com.wx.orm.entity.ShopConfigDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.service.OrderService;
import com.wx.service.ShopConfigService;
import com.wx.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WxPayServiceImpl implements PayService {

    @Autowired
    private Config  wechatPayConfig;

    @Autowired
    private WxPayUtil wxPayUtil;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShopConfigService shopConfigService;

    @Autowired
    private UserProfileService userProfileService;

    @Value("${wx.miniapp-pay.appId}")
    private String appId;

    @Value("${wx.miniapp-pay.mchId}")
    private String mchId;

    @Value("${wx.miniapp-pay.payFlag}")
    private String payFlag;

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
    public Map<String, String> createJsapiPayment(String token, String orderNo, String from) {
        JsapiService service = new JsapiService.Builder().config(wechatPayConfig).build();

        // 通过订单获取订单金额
        GoodsHistoryDO goodsHistoryDO = orderService.queryOrderByTradeNo(orderNo);
        PrepayRequest request = new PrepayRequest();
        Amount amountObj = new Amount();
        amountObj.setTotal(goodsHistoryDO.getPayAmount().multiply(new BigDecimal(100)).intValue());
        request.setAmount(amountObj);
        request.setAppid(appId);
        request.setMchid(mchId);

        String fromMall = goodsHistoryDO.getFromMall();
        if (!Objects.equals(from, fromMall)) {
            throw new RuntimeException("订单来源与商户不匹配");
        }
        ShopConfigDO shopConfigDO = shopConfigService.queryMerchantConfigByFrom(fromMall);
        request.setDescription(shopConfigDO.getCompanyName() + " - 商品");
        request.setNotifyUrl(notifyUrl);
        request.setOutTradeNo(orderNo);
        Payer payer = new Payer();
        UserProfileDO user = userProfileService.getUserByToken(token);
        payer.setOpenid(user.getOpenId());
        request.setPayer(payer);

        Map<String, String> attachMap = new HashMap<>();
        attachMap.put("orderId", orderNo);
        attachMap.put("merchantId", payFlag);
        request.setAttach(JSON.toJSONString(attachMap));

        PrepayResponse response = service.prepay(request);

        orderService.updatePayway(orderNo, PayWayEnums.WECHAT);

        Map<String, String> result = new HashMap<>();
        result.put("appId", appId);
        result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("nonceStr", wxPayUtil.generateNonceStr());
        result.put("package", "prepay_id=" + response.getPrepayId());
        result.put("signType", "RSA");
        result.put("paySign", wxPayUtil.generateSignature(result));
        return result;
    }
}
