package com.wx.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wx.common.enums.OrderStatus;
import com.wx.common.enums.PayWayEnums;
import com.wx.common.model.Response;
import com.wx.common.model.request.GetOrderDetailByTradeNo;
import com.wx.common.model.request.PaymentRequest;
import com.wx.common.model.request.QueryOrderGoodsModel;
import com.wx.common.model.request.ReturnRequest;
import com.wx.common.model.response.QueryOrderHistoryModel;
import com.wx.common.model.response.ReturnResponse;
import com.wx.common.utils.QrCodeUtil;
import com.wx.orm.entity.GoodsHistoryDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.service.OrderService;
import com.wx.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.wx.common.config.PayConstants.*;

@Slf4j
@RestController
@RequestMapping("/pay/v3")
@RequiredArgsConstructor
public class WxPayController {
    private final NativePayService payService;
    private final JsapiService jsapiService;
    private final OrderService orderService;
    private final NotificationParser notificationParser;
    private final TokenService tokenService;

    @PostMapping("/createOrderNative")
    public Response<String> createOrderNative(@RequestBody PaymentRequest paymentRequest) throws JsonProcessingException {
        String from = paymentRequest.getFrom();
        if (StringUtils.isBlank(from)) {
            return Response.failure("from不能为空");
        }
        String tradeNo = paymentRequest.getTradeNo();
        if (StringUtils.isBlank(tradeNo)) {
            return Response.failure("tradeNo不能为空");
        }
        QueryOrderHistoryModel orderHistory = orderService.getOrderDetailById(new GetOrderDetailByTradeNo(tradeNo));
        if (orderHistory != null && orderHistory.getIsComplete() == 2) {
            return Response.failure("订单已支付");
        }

        // update order status to waiting payment, update pay_way to wxPay
        orderService.updateOrderStatus(tradeNo, OrderStatus.WAITING_PAYMENT);
        orderService.updatePayway(tradeNo, PayWayEnums.WECHAT);
        // 通过from 查出对应的商户配置
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(1);
        request.setAmount(amount);
        request.setAppid(APP_ID);
        request.setMchid(MERCHANT_ID);
        request.setDescription("商品描述(from db)");
        request.setNotifyUrl(CALLBACK_URL);
//        request.setNotifyUrl(LOCAL_CALLBACK_URL);
        // String orderId = OrderUtil.snowflakeOrderNo();
        request.setOutTradeNo(tradeNo);
        Map<String, String> attachMap = new HashMap<>();
        attachMap.put("orderId", tradeNo);
        attachMap.put("mchId", "购买商品所在的商户号(mallapp)");
        request.setAttach(JSON.toJSONString(attachMap));
        try {
            PrepayResponse prepay = payService.prepay(request);
            String qrCodeBase64 = QrCodeUtil.generateQrCodeBase64(prepay.getCodeUrl(), 300);
            if (StringUtils.isNoneBlank(prepay.getCodeUrl())) {
                // 更新订单的支付方式 为 1 wx
                orderService.updatePayway(tradeNo, PayWayEnums.WECHAT);
            }
            return Response.success(qrCodeBase64);
        } catch (Exception e) {
            log.error("本次调用又出错了...", e);
            return Response.failure("生成二维码异常...");
        }
    }

    @PostMapping("/createOrderJSAPI")
    public Response<String> createOrderJSAPI(@RequestBody PaymentRequest paymentRequest) throws JsonProcessingException {
        String from = paymentRequest.getFrom();
        if (StringUtils.isBlank(from)) {
            return Response.failure("from不能为空");
        }
        String tradeNo = paymentRequest.getTradeNo();
        if (StringUtils.isBlank(tradeNo)) {
            return Response.failure("tradeNo不能为空");
        }
        QueryOrderHistoryModel orderHistory = orderService.getOrderDetailById(new GetOrderDetailByTradeNo(tradeNo));
        if (orderHistory != null && orderHistory.getIsComplete() == 2) {
            return Response.failure("订单已支付");
        }

        // update order status to waiting payment, update pay_way to wxPay
        orderService.updateOrderStatus(tradeNo, OrderStatus.WAITING_PAYMENT);
        orderService.updatePayway(tradeNo, PayWayEnums.WECHAT);
        // 通过from 查出对应的商户配置
        try {
            com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest request2 = new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();
            request2.setAppid(APP_ID);
            request2.setMchid(MERCHANT_ID);
            request2.setDescription("商品描述(from db)");
            request2.setNotifyUrl(CALLBACK_URL);
            request2.setOutTradeNo(tradeNo);
            Map<String, String> attachMap = new HashMap<>();
            attachMap.put("orderId", tradeNo);
            attachMap.put("mchId", "购买商品所在的商户号(mallapp)");
            request2.setAttach(JSON.toJSONString(attachMap));
            com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse prepay = jsapiService.prepay(request2);
            String prepayId = prepay.getPrepayId();
            if (StringUtils.isNoneBlank(prepayId)) {
                // 更新订单的支付方式 为 1 wx
                orderService.updatePayway(tradeNo, PayWayEnums.WECHAT);
            }
            return Response.success(prepayId);
        } catch (Exception e) {
            log.error("本次调用又出错了...", e);
            return Response.failure("生成二维码异常...");
        }
    }


    @PostMapping("/return")
    public Response<ReturnResponse> returnUrl(@RequestBody ReturnRequest request) throws JsonProcessingException {
        String token = request.getToken();
        if (Objects.isNull(token)) {
            return Response.failure("token不能为空");
        }

        UserProfileDO userByToken = tokenService.getUserByToken(token);
        if (Objects.isNull(userByToken)) {
            return Response.failure("没有登录");
        }

        QueryOrderHistoryModel orderDetailById = orderService.getOrderDetailById(new GetOrderDetailByTradeNo(request.getTradeNo()));
        if (Objects.isNull(orderDetailById)) {
            return Response.failure("订单不存在");
        }
        if (orderDetailById.getIsPaySuccess() == 2) {
            ReturnResponse returnResponse = new ReturnResponse(request.getTradeNo(), "1", OrderStatus.PAID.name(),
                    orderDetailById.getPayAmount(), orderDetailById.getOrderTime());

            return Response.success(returnResponse);
        }
        // 处理支付成功后的逻辑
        ReturnResponse successResponse = new ReturnResponse(request.getTradeNo(), "1", OrderStatus.WAITING_PAYMENT.name(),
                orderDetailById.getPayAmount(), orderDetailById.getOrderTime());
        return Response.success(successResponse);
    }

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public ResponseEntity<String> callback(HttpServletRequest request) {
        try {
            // 1. 获取请求头信息
            Map<String, String> headers = getHeaders(request);
            String timestamp = headers.get("Wechatpay-Timestamp");
            String nonce = headers.get("Wechatpay-Nonce");
            String signature = headers.get("Wechatpay-Signature");
            String serial = headers.get("Wechatpay-Serial");
            String body = getRequestBody(request);

            // 2. 构造RequestParam
            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(serial)
                    .nonce(nonce)
                    .signature(signature)
                    .timestamp(timestamp)
                    .body(body)
                    .build();

            // 3. 解析并验证通知
            Transaction transaction = notificationParser.parse(requestParam, Transaction.class);

            // 4. 处理业务逻辑
            return ResponseEntity.ok(processTransaction(transaction));

        } catch (Exception e) {
            // 记录错误日志
            return ResponseEntity.badRequest().body("fail");
        }
    }


    private String processTransaction(Transaction transaction) {
        // 验证订单状态
        if (!Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())) {
            return buildErrorResponse("FAIL", "支付未成功");
        }

        String outTradeNo = transaction.getOutTradeNo();
        String transactionId = transaction.getTransactionId();
        int amount = transaction.getAmount().getTotal(); // 总金额(分)
        log.info("订单支付成功：{}", transaction);
        try {

            // TODO: 实现你的业务逻辑
            // 注意：必须做幂等处理，防止重复通知
            boolean success = processOrder(outTradeNo, transactionId, amount);

            return success ? buildSuccessResponse() : buildErrorResponse("FAIL", "订单处理失败");
        } catch (Exception e) {
            return buildErrorResponse("FAIL", "系统异常: " + e.getMessage());
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();


    private boolean processOrder(String outTradeNo, String transactionId, int amount) throws JsonProcessingException {
        // 实现你的业务逻辑，例如：
        // 1. 检查订单是否存在
        // 2. 验证金额是否匹配
        // 3. 更新订单状态
        // 4. 记录支付信息
        // 返回处理结果
        System.out.println("处理订单：" + outTradeNo);
        System.out.println("订单支付成功：" + transactionId);
        System.out.println("支付金额：" + amount);

//        orderService.updateOrderStatus(outTradeNo, OrderStatus.PAID);
        // 查询订单是否存在
        GoodsHistoryDO orderDetail = orderService.queryOrderByTradeNo(outTradeNo);
        if (Objects.isNull(orderDetail)) {
            return false;
        }

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
                new TypeReference<List<QueryOrderGoodsModel>>() {
                }
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
