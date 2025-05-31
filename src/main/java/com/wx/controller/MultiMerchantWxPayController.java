package com.wx.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wx.common.config.MultiMerchantWxPayConfig;
import com.wx.common.enums.OrderStatus;
import com.wx.common.enums.PayWayEnums;
import com.wx.common.model.Response;
import com.wx.common.model.request.GetOrderDetailByTradeNo;
import com.wx.common.model.request.QueryOrderGoodsModel;
import com.wx.common.model.request.ReturnRequest;
import com.wx.common.model.response.QueryOrderHistoryModel;
import com.wx.common.model.response.ReturnResponse;
import com.wx.common.utils.QrCodeUtil;
import com.wx.orm.entity.GoodsHistoryDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.service.OrderService;
import com.wx.service.TokenService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/pay/v3/multi")
@AllArgsConstructor
public class MultiMerchantWxPayController {
    private final MultiMerchantWxPayConfig multiMerchantConfig;
    private final OrderService orderService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Data
    public static class MultiMerchantPaymentRequest {
        private String merchantId; // 商户ID
        private String tradeNo;    // 交易号
        private String from;       // 来源
        private String description; // 商品描述
        private Integer amount;    // 金额（分）
    }

    @PostMapping("/createOrderNative")
    public Response<String> createOrderNative(@RequestBody MultiMerchantPaymentRequest paymentRequest) {
        // 验证参数
        if (StringUtils.isBlank(paymentRequest.getMerchantId())) {
            return Response.failure("商户ID不能为空");
        }
        if (StringUtils.isBlank(paymentRequest.getFrom())) {
            return Response.failure("from不能为空");
        }
        if (StringUtils.isBlank(paymentRequest.getTradeNo())) {
            return Response.failure("tradeNo不能为空");
        }
        if (paymentRequest.getAmount() == null || paymentRequest.getAmount() <= 0) {
            return Response.failure("金额必须大于0");
        }

        // 获取商户配置
        MultiMerchantWxPayConfig.MerchantConfig merchantConfig = multiMerchantConfig.getMerchantConfig(paymentRequest.getMerchantId());
        if (merchantConfig == null) {
            return Response.failure("商户配置不存在");
        }

        // 获取商户支付服务
        NativePayService payService = multiMerchantConfig.getNativePayService(paymentRequest.getMerchantId());

        try {
            // 检查订单状态
            QueryOrderHistoryModel orderHistory = orderService.getOrderDetailById(
                    GetOrderDetailByTradeNo.builder().tradeNo(paymentRequest.getTradeNo()).build());
            if (orderHistory != null && orderHistory.getIsComplete() == 2) {
                return Response.failure("订单已支付");
            }

            // 更新订单状态为等待支付，支付方式为微信支付
            orderService.updateOrderStatus(paymentRequest.getTradeNo(), OrderStatus.WAITING_PAYMENT);
            orderService.updatePayway(paymentRequest.getTradeNo(), PayWayEnums.WECHAT);

            // 构建支付请求
            PrepayRequest request = new PrepayRequest();
            Amount amount = new Amount();
            amount.setTotal(paymentRequest.getAmount());
            request.setAmount(amount);
            request.setAppid(merchantConfig.getAppId());
            request.setMchid(merchantConfig.getMerchantId());
            request.setDescription(StringUtils.isNotBlank(paymentRequest.getDescription()) ?
                    paymentRequest.getDescription() : "商品购买");
            request.setNotifyUrl(merchantConfig.getNotifyUrl());
            request.setOutTradeNo(paymentRequest.getTradeNo());

            // 附加数据，用于回调时识别
            Map<String, String> attachMap = new HashMap<>();
            attachMap.put("orderId", paymentRequest.getTradeNo());
            attachMap.put("merchantId", paymentRequest.getMerchantId());
            request.setAttach(JSON.toJSONString(attachMap));

            // 调用微信支付API
            PrepayResponse prepay = payService.prepay(request);
            String qrCodeBase64 = QrCodeUtil.generateQrCodeBase64(prepay.getCodeUrl(), 300);

            if (StringUtils.isNotBlank(prepay.getCodeUrl())) {
                // 更新订单的支付方式为微信支付
                orderService.updatePayway(paymentRequest.getTradeNo(), PayWayEnums.WECHAT);
            }

            return Response.success(qrCodeBase64);
        } catch (Exception e) {
            log.error("生成支付二维码异常", e);
            return Response.failure("生成二维码异常: " + e.getMessage());
        }
    }

    @PostMapping("/return")
    public Response<ReturnResponse> returnUrl(@RequestBody ReturnRequest request) throws JsonProcessingException {
        // 验证token
        String token = request.getToken();
        if (Objects.isNull(token)) {
            return Response.failure("token不能为空");
        }

        UserProfileDO userByToken = tokenService.getUserByToken(token);
        if (Objects.isNull(userByToken)) {
            return Response.failure("没有登录");
        }

        // 查询订单
        QueryOrderHistoryModel orderDetailById = orderService.getOrderDetailById(
                GetOrderDetailByTradeNo.builder().tradeNo(request.getTradeNo()).build());
        if (Objects.isNull(orderDetailById)) {
            return Response.failure("订单不存在");
        }

        // 返回订单状态
        if (orderDetailById.getIsPaySuccess() == 2) {
            ReturnResponse returnResponse = new ReturnResponse(
                    request.getTradeNo(),
                    "1",
                    OrderStatus.PAID.name(),
                    orderDetailById.getPayAmount(),
                    orderDetailById.getOrderTime()
            );
            return Response.success(returnResponse);
        }

        // 处理支付成功后的逻辑
        ReturnResponse successResponse = new ReturnResponse(
                request.getTradeNo(),
                "1",
                OrderStatus.WAITING_PAYMENT.name(),
                orderDetailById.getPayAmount(),
                orderDetailById.getOrderTime()
        );
        return Response.success(successResponse);
    }

    @RequestMapping(value = "/callback/{merchantId}", method = RequestMethod.POST)
    public ResponseEntity<String> callback(@PathVariable String merchantId, HttpServletRequest request) {
        try {
            // 验证商户ID
            if (StringUtils.isBlank(merchantId)) {
                return ResponseEntity.badRequest().body(buildErrorResponse("FAIL", "商户ID不能为空"));
            }

            // 获取商户特定的通知解析器
            NotificationParser notificationParser;
            try {
                notificationParser = multiMerchantConfig.getNotificationParser(merchantId);
            } catch (IllegalArgumentException e) {
                log.error("商户配置不存在: {}", merchantId);
                return ResponseEntity.badRequest().body(buildErrorResponse("FAIL", "商户配置不存在"));
            }

            // 获取请求头信息
            Map<String, String> headers = getHeaders(request);
            String timestamp = headers.get("Wechatpay-Timestamp");
            String nonce = headers.get("Wechatpay-Nonce");
            String signature = headers.get("Wechatpay-Signature");
            String serial = headers.get("Wechatpay-Serial");
            String body = getRequestBody(request);

            // 构造RequestParam
            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(serial)
                    .nonce(nonce)
                    .signature(signature)
                    .timestamp(timestamp)
                    .body(body)
                    .build();

            // 解析并验证通知
            Transaction transaction = notificationParser.parse(requestParam, Transaction.class);

            // 处理业务逻辑
            return ResponseEntity.ok(processTransaction(transaction, merchantId));

        } catch (Exception e) {
            log.error("处理支付回调异常", e);
            return ResponseEntity.badRequest().body(buildErrorResponse("FAIL", "系统异常: " + e.getMessage()));
        }
    }

    private String processTransaction(Transaction transaction, String merchantId) {
        // 验证订单状态
        if (!Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
            return buildErrorResponse("FAIL", "支付未成功");
        }

        String outTradeNo = transaction.getOutTradeNo();
        String transactionId = transaction.getTransactionId();
        int amount = transaction.getAmount().getTotal(); // 总金额(分)

        // 解析附加数据，验证商户ID
        try {
            String attachStr = transaction.getAttach();
            Map<String, String> attachMap = objectMapper.readValue(attachStr, new TypeReference<Map<String, String>>() {});
            String attachMerchantId = attachMap.get("merchantId");

            if (!merchantId.equals(attachMerchantId)) {
                log.error("商户ID不匹配: 回调路径={}, 附加数据={}", merchantId, attachMerchantId);
                return buildErrorResponse("FAIL", "商户ID不匹配");
            }
        } catch (Exception e) {
            log.error("解析附加数据异常", e);
            return buildErrorResponse("FAIL", "解析附加数据异常");
        }

        log.info("订单支付成功：商户ID={}, 订单号={}, 微信支付单号={}, 金额={}",
                merchantId, outTradeNo, transactionId, amount);

        try {
            // 处理订单
            boolean success = processOrder(outTradeNo, transactionId, amount);
            return success ? buildSuccessResponse() : buildErrorResponse("FAIL", "订单处理失败");
        } catch (Exception e) {
            log.error("处理订单异常", e);
            return buildErrorResponse("FAIL", "系统异常: " + e.getMessage());
        }
    }

    private boolean processOrder(String outTradeNo, String transactionId, int amount) throws JsonProcessingException {
        // 查询订单是否存在
        GoodsHistoryDO orderDetail = orderService.queryOrderByTradeNo(outTradeNo);
        if (Objects.isNull(orderDetail)) {
            log.error("订单不存在: {}", outTradeNo);
            return false;
        }

        // 验证订单金额（实际应用中应该验证金额是否匹配）
        // TODO: 验证订单金额

        // 更新订单状态
        orderService.updateOrderStatus(outTradeNo, OrderStatus.PAID);

        // 更新销量 goods 表的 sales 字段
        String goodsListStr = orderDetail.getGoodsList();
        String normalizedJson = goodsListStr
                .replace("\\\"", "\"")
                .replace("\"[", "[")
                .replace("]\"", "]");

        List<QueryOrderGoodsModel> queryOrderGoodsModels = objectMapper.readValue(
                normalizedJson,
                new TypeReference<List<QueryOrderGoodsModel>>() {}
        );

        for (QueryOrderGoodsModel goodsModel : queryOrderGoodsModels) {
            orderService.updateSales(goodsModel.getId(), goodsModel.getNum());
        }

        return true;
    }

    private String buildSuccessResponse() {
        return "{\"code\": \"SUCCESS\", \"message\": \"OK\"}";
    }

    private String buildErrorResponse(String code, String message) {
        return String.format("{\"code\": \"%s\", \"message\": \"%s\"}", code, message);
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
