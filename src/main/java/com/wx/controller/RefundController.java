package com.wx.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import com.wechat.pay.java.service.refund.model.Status;
import com.wx.common.config.MultiMerchantAlipayConfig;
import com.wx.common.config.MultiMerchantWxPayConfig;
import com.wx.common.enums.OrderStatus;
import com.wx.common.enums.PayWayEnums;
import com.wx.common.model.Response;
import com.wx.common.model.request.GetOrderDetailByTradeNo;
import com.wx.common.model.response.QueryOrderHistoryModel;
import com.wx.orm.entity.ShopConfigDO;
import com.wx.service.OrderService;
import com.wx.service.ShopConfigService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/pay/refund")
@AllArgsConstructor
public class RefundController {

    private final MultiMerchantAlipayConfig alipayConfig;
    private final MultiMerchantWxPayConfig wxPayConfig;
    private final OrderService orderService;
    private final ShopConfigService shopConfigService;

    @Data
    public static class RefundRequest {
        private String tradeNo;        // 原交易订单号
        private String refundReason;   // 退款原因
        private String from;           // 来源标识
//        private Double refundAmount;   // 退款金额
    }

    @Data
    public static class RefundResponse {
        private String refundNo;      // 退款单号
        private String status;        // 退款状态
        private String message;       // 返回信息

        public RefundResponse(String refundNo, String status, String message) {
            this.refundNo = refundNo;
            this.status = status;
            this.message = message;
        }
    }

    @PostMapping("/apply")
    public Response<RefundResponse> applyRefund(@RequestBody RefundRequest request) {
        try {
            // 参数验证
            if (StringUtils.isBlank(request.getTradeNo())) {
                return Response.failure("订单号不能为空");
            }
            if (StringUtils.isBlank(request.getFrom())) {
                return Response.failure("来源标识不能为空");
            }

            // 查询订单信息
            QueryOrderHistoryModel order = orderService.getOrderDetailById(
                    new GetOrderDetailByTradeNo(request.getTradeNo()));
            if (order == null) {
                return Response.failure("订单不存在");
            }

            // 验证订单状态
            if (order.getIsPaySuccess() != 2) {
                return Response.failure("订单未支付，不能退款");
            }
            if (OrderStatus.REFUNDED.getCode() == order.getStatus()) {
                return Response.failure("订单已退款");
            }

            // 验证退款金额
//            if (request.getRefundAmount() > order.getPayAmount()) {
//                return Response.failure("退款金额不能大于支付金额");
//            }

            // 获取商户配置
            ShopConfigDO shopConfig = shopConfigService.queryMerchantConfigByFrom(request.getFrom());
            if (shopConfig == null || StringUtils.isBlank(shopConfig.getPaymentFlag())) {
                return Response.failure("商户配置不存在");
            }

            String merchantId = shopConfig.getPaymentFlag();
            String refundNo = generateRefundNo(request.getTradeNo());

            // 根据支付方式调用对应的退款接口
            if (PayWayEnums.ALIPAY.getValue().equals(order.getPayWay())) {
                return handleAlipayRefund(merchantId, request, order, refundNo);
            } else if (PayWayEnums.WECHAT.getValue().equals(order.getPayWay())) {
                return handleWxPayRefund(merchantId, request, order, refundNo);
            } else {
                return Response.failure("不支持的支付方式");
            }

        } catch (Exception e) {
            log.error("申请退款异常", e);
            return Response.failure("申请退款失败: " + e.getMessage());
        }
    }

    private Response<RefundResponse> handleAlipayRefund(String merchantId, RefundRequest request,
                                                        QueryOrderHistoryModel order, String refundNo)
            throws AlipayApiException {
        // 获取支付宝客户端
        AlipayClient alipayClient = alipayConfig.getAlipayClient(merchantId);

        // 创建退款请求
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(request.getTradeNo());
        model.setRefundAmount(String.format("%.2f", order.getPayAmount().doubleValue()));
        model.setOutRequestNo(refundNo);
        model.setRefundReason(request.getRefundReason());
        alipayRequest.setBizModel(model);

        // 调用支付宝退款接口
        AlipayTradeRefundResponse response = alipayClient.execute(alipayRequest);
        if (response.isSuccess()) {
            // 更新订单状态
            orderService.updateOrderStatus(request.getTradeNo(), OrderStatus.REFUNDED);
            return Response.success(new RefundResponse(
                    refundNo,
                    "SUCCESS",
                    "退款成功"
            ));
        } else {
            log.error("支付宝退款失败: {}", response.getBody());
            return Response.failure(
                    refundNo + " 退款失败: " + response.getSubMsg()
            );
        }
    }

    private Response<RefundResponse> handleWxPayRefund(String merchantId, RefundRequest request,
                                                       QueryOrderHistoryModel order, String refundNo) {
        try {
            // 获取微信支付退款服务
            RefundService refundService = wxPayConfig.getRefundService(merchantId);

            // 创建退款金额对象
            AmountReq amount = new AmountReq();
            amount.setRefund(order.getPayAmount().multiply(BigDecimal.valueOf(100)).longValue()); // 转换为分
            amount.setTotal(order.getPayAmount().multiply(BigDecimal.valueOf(100)).longValue());
            amount.setCurrency("CNY");

            MultiMerchantWxPayConfig.MerchantConfig merchantConfig = wxPayConfig.getMerchantConfig(merchantId);
            // 创建退款请求
            CreateRequest wxRequest = new CreateRequest();
            wxRequest.setOutTradeNo(request.getTradeNo());
            wxRequest.setOutRefundNo(refundNo);
            wxRequest.setReason(request.getRefundReason());
            wxRequest.setAmount(amount);
            wxRequest.setNotifyUrl(merchantConfig.getRefundNotifyUrl());

            // 调用微信退款接口
            Refund refund = refundService.create(wxRequest);
            if (Status.SUCCESS.equals(refund.getStatus())) {
                // 更新订单状态
                orderService.updateOrderStatus(request.getTradeNo(), OrderStatus.REFUNDED);
                return Response.success(new RefundResponse(
                        refundNo,
                        refund.getStatus().name(),
                        "退款成功"
                ));
            } else {
                log.error("微信退款失败: {}", refund);
                return Response.failure(
                        refundNo + " 退款失败: " + refund.getStatus().name()
                );
            }
        } catch (Exception e) {
            log.error("微信退款异常", e);
            return Response.failure(
                    refundNo + " 退款失败: " + e.getMessage()
            );
        }
    }

    @PostMapping("/notify/alipay/{merchantId}")
    public String alipayRefundNotify(@PathVariable String merchantId, HttpServletRequest request) {
        // TODO: 实现支付宝退款回调处理
        log.info("支付宝退款回调通知");
        log.info(request.getParameterMap().toString());
        return "success";
    }

    @PostMapping("/notify/wxpay/{merchantId}")
    public String wxPayRefundNotify(@PathVariable String merchantId, HttpServletRequest request) throws Exception {
        // TODO: 实现微信支付退款回调处理
        log.info("微信退款回调通知");
        String requestBody = getRequestBody(request);
        log.info("  => {}", requestBody);
        return "success";
    }

    @GetMapping("/query/{refundNo}")
    public Response<RefundResponse> queryRefundStatus(@PathVariable String refundNo) {
        // TODO: 实现退款状态查询
        return Response.success(new RefundResponse(refundNo, "PROCESSING", "退款处理中"));
    }

    private String generateRefundNo(String tradeNo) {
        // 生成退款单号：原订单号_退款时间戳_随机数
        return String.format("%s_RF%d_%s",
                tradeNo,
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 8));
    }


    private String getRequestBody(HttpServletRequest request) throws Exception {
        return request.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        return headers;
    }
}
