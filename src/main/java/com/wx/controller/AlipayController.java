package com.wx.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wx.common.config.AlipayConfig;
import com.wx.common.enums.OrderStatus;
import com.wx.common.model.Response;
import com.wx.common.model.request.GetOrderDetailByTradeNo;
import com.wx.common.model.request.PaymentRequest;
import com.wx.common.model.request.ReturnRequest;
import com.wx.common.model.response.QueryOrderHistoryModel;
import com.wx.common.model.response.ReturnResponse;
import com.wx.common.utils.OrderUtil;
import com.wx.orm.entity.UserProfileDO;
import com.wx.service.AlipayService;
import com.wx.service.OrderService;
import com.wx.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/alipay")
@RequiredArgsConstructor
public class AlipayController {
    private final OrderService orderService;
    private final TokenService tokenService;
    @GetMapping("/pay")
    public String pay(PaymentRequest request) {
        // 生成商户订单号
        String outTradeNo = OrderUtil.snowflakeOrderNo();
        // 支付金额
        String totalAmount = "0.01";

        // 调用支付
        return AlipayService.pagePay(outTradeNo, totalAmount, request.getSubject());
    }

    // 支付成功同步回调
    @PostMapping("/return")
    public Response<ReturnResponse> returnUrl(@RequestBody ReturnRequest request) throws JsonProcessingException {
        // 处理支付成功后的逻辑
        String token = request.getToken();
        if (Objects.isNull(token)) {
            return Response.failure("token不能为空");
        }
        UserProfileDO userByToken = tokenService.getUserByToken(token);
        if (Objects.isNull(userByToken)) {
            return Response.failure("没有登录");
        }

        QueryOrderHistoryModel orderDetailById = orderService.getOrderDetailById(GetOrderDetailByTradeNo.builder().tradeNo(request.getTradeNo()).build());
        if (Objects.isNull(orderDetailById)) {
            return Response.failure("订单不存在");
        }
        if (orderDetailById.getIsPaySuccess() == 2) {
            ReturnResponse returnResponse = new ReturnResponse(request.getTradeNo(), "微信支付", OrderStatus.PAID.name(),
                    orderDetailById.getPayAmount(), orderDetailById.getOrderTime());

            return Response.success(returnResponse);
        }
        // 处理支付成功后的逻辑
        return Response.failure(OrderStatus.WAITING_PAYMENT.name());
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