package com.wx.miniapp.controller;

import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wx.miniapp.dto.CreatePaymentRequest;
import com.wx.miniapp.service.PayService;
import com.wx.miniapp.util.WxPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/wechat/miniapp/pay")
public class MiniAppPayController {

    @Autowired
    private PayService payService;

    @Autowired
    private NotificationParser parser;


    @PostMapping("/create")
    public Map<String, String> createPayment(@RequestBody CreatePaymentRequest request) {
        return payService.createJsapiPayment(request.getOpenid(),
                request.getOrderNo(), request.getAmount(), request.getDescription());
    }

    @PostMapping("/notify")
    public String handlePaymentNotify(HttpServletRequest request, @RequestBody String body) {
        try {
            // 构建回调参数
            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(request.getHeader("Wechatpay-Serial"))
                    .nonce(request.getHeader("Wechatpay-Nonce"))
                    .signature(request.getHeader("Wechatpay-Signature"))
                    .timestamp(request.getHeader("Wechatpay-Timestamp"))
                    .body(body)
                    .build();

            // 解析回调数据
            Transaction transaction = parser.parse(requestParam, Transaction.class);

            // 处理支付结果
            if (Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
                boolean success = payService.processPayment(
                        transaction.getOutTradeNo(),
                        transaction.getTransactionId(),
                        transaction.getAmount().getTotal()
                );
                return success ? WxPayUtil.generateSuccessResponse()
                        : WxPayUtil.generateErrorResponse("处理支付结果失败");
            }
            return WxPayUtil.generateErrorResponse("支付未成功");
        } catch (Exception e) {
            return WxPayUtil.generateErrorResponse("回调处理异常: " + e.getMessage());
        }
    }
}
