package com.wx.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.wx.common.config.AlipayConfig;
import com.wx.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
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
        // 1. 将请求参数转换为Map
        Map<String, String> params = convertRequestParamsToMap(request);

        try {
            // 2. 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    AlipayConfig.ALIPAY_PUBLIC_KEY,
                    AlipayConfig.CHARSET,
                    AlipayConfig.SIGN_TYPE);

            if (!signVerified) {
                log.error("支付宝回调签名验证失败");
                return "failure";
            }

            // 3. 处理业务逻辑
            String tradeStatus = params.get("trade_status");

            if ("TRADE_SUCCESS".equals(tradeStatus) ||
                    "TRADE_FINISHED".equals(tradeStatus)) {
                // 支付成功处理逻辑
                String outTradeNo = params.get("out_trade_no");
                String tradeNo = params.get("trade_no");
                String totalAmount = params.get("total_amount");

                log.info("支付宝支付成功，订单号：{}, 支付宝交易号：{}, 金额：{}",
                        outTradeNo, tradeNo, totalAmount);

                // TODO: 更新订单状态、发货等业务逻辑
                // 注意要做幂等处理，防止重复通知

                return "success"; // 必须返回success（小写）
            }
        } catch (AlipayApiException e) {
            log.error("支付宝回调处理异常", e);
        }

        return "failure";
    }

    private Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();

        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        return params;
    }
}