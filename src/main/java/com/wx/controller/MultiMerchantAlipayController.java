package com.wx.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wx.common.config.MultiMerchantAlipayConfig;
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
import com.wx.orm.entity.ShopConfigDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.service.OrderService;
import com.wx.service.ShopConfigService;
import com.wx.service.TokenService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/pay/v3/alipay")
@AllArgsConstructor
public class MultiMerchantAlipayController {

    private final MultiMerchantAlipayConfig multiMerchantConfig;
    private final OrderService orderService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ShopConfigService shopConfigService;

    @Data
    public static class AlipayPaymentRequest {
        private String merchantId;  // 商户ID
        private String tradeNo;     // 交易号
        private String from;        // 来源
        private String description; // 商品描述
        private String subject;     // 订单标题
        private Integer amount;     // 金额（分）
    }

    @PostMapping("/createOrderNative")
    public Response<String> createOrderNative(@RequestBody AlipayPaymentRequest paymentRequest) {
        String from = paymentRequest.getFrom();
        String tradeNo = paymentRequest.getTradeNo();
        // 通过from 查询订单所属的商户的支付标识
        ShopConfigDO shopConfig = shopConfigService.queryMerchantConfigByFrom(from);
        // 验证参数
        if (shopConfig == null || StringUtils.isBlank(shopConfig.getPaymentFlag())) {
            return Response.failure("商户ID不能为空");
        }
        if (StringUtils.isBlank(tradeNo)) {
            return Response.failure("tradeNo不能为空");
        }
//        if (paymentRequest.getAmount() == null || paymentRequest.getAmount() <= 0) {
//            return Response.failure("金额必须大于0");
//        }

        String merchantId = shopConfig.getPaymentFlag();
        try {
            // 获取商户配置和客户端
            MultiMerchantAlipayConfig.MerchantConfig merchantConfig = 
                    multiMerchantConfig.getMerchantConfig(merchantId);
            AlipayClient alipayClient = multiMerchantConfig.getAlipayClient(merchantId);

            // 检查订单状态
            QueryOrderHistoryModel orderHistory = orderService.getOrderDetailById(
                    new GetOrderDetailByTradeNo(tradeNo));
            if (orderHistory != null && orderHistory.getIsComplete() == 2) {
                return Response.failure("订单已支付");
            }

            // 更新订单状态为等待支付，支付方式为支付宝
            orderService.updateOrderStatus(tradeNo, OrderStatus.WAITING_PAYMENT);
            orderService.updatePayway(tradeNo, PayWayEnums.ALIPAY);

            // 创建预下单请求
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            request.setNotifyUrl(merchantConfig.getNotifyUrl());
            request.setReturnUrl(merchantConfig.getReturnUrl());

            // 设置业务参数
            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
            model.setOutTradeNo(tradeNo);
            if (orderHistory != null) {
                model.setTotalAmount(String.format("%.2f", orderHistory.getPayAmount()));
            }
            model.setSubject(StringUtils.isNotBlank(paymentRequest.getSubject()) ?
                    paymentRequest.getSubject() : "商品购买");
            model.setBody(StringUtils.isNotBlank(paymentRequest.getDescription()) ?
                    paymentRequest.getDescription() : "商品购买");
            model.setTimeoutExpress("15m");

            // 设置商户ID到扩展参数中
//            ExtendParams extendParams = new ExtendParams();
//            extendParams.setSysServiceProviderId(paymentRequest.getMerchantId());
//            extendParams.put("merchantId", paymentRequest.getMerchantId());
//            model.setExtendParams(extendParams);

            request.setBizModel(model);

            // 调用支付宝接口
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                log.error("创建支付宝订单失败: {}", response.getBody());
                return Response.failure("创建订单失败: " + response.getSubMsg());
            }

            log.info("创建支付宝订单成功: {}", response.getBody());
            log.info("支付二维码 {}", response.getQrCode());
            // 生成二维码
            String qrCodeBase64 = QrCodeUtil.generateQrCodeBase64(response.getQrCode(), 300);
            return Response.success(qrCodeBase64);

        } catch (Exception e) {
            log.error("生成支付宝二维码异常", e);
            return Response.failure("生成二维码异常: " + e.getMessage());
        }
    }

    @PostMapping("/notify/{merchantId}")
    public String notify(@PathVariable String merchantId, HttpServletRequest request) {
        Map<String, String> params = convertRequestParametersToMap(request);
        log.info("收到支付宝回调通知，商户ID: {}, 参数: {}", merchantId, params);

        try {
            // 获取商户配置
            MultiMerchantAlipayConfig.MerchantConfig merchantConfig =
                    multiMerchantConfig.getMerchantConfig(merchantId);

            // 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    merchantConfig.getPublicKey(),
                    merchantConfig.getCharset(),
                    merchantConfig.getSignType()
            );

            if (!signVerified) {
                log.error("支付宝回调签名验证失败");
                return "failure";
            }

            log.info("验证签名成功 {}", params);
            // 验证商户ID
//            String notifyMerchantId = params.get("merchantId");
//            if (!merchantId.equals(notifyMerchantId)) {
//                log.error("商户ID不匹配: 回调路径={}, 通知参数={}", merchantId, notifyMerchantId);
//                return "failure";
//            }

            // 处理支付结果
            String tradeStatus = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                String outTradeNo = params.get("out_trade_no");
                String tradeNo = params.get("trade_no");
                String totalAmount = params.get("total_amount");

                // 将金额转换为分
                int amount = (int) (Double.parseDouble(totalAmount) * 100);

                // 处理订单
                boolean success = processOrder(outTradeNo, tradeNo, amount);
                return success ? "success" : "failure";
            }

            return "success";

        } catch (Exception e) {
            log.error("处理支付宝回调异常", e);
            return "failure";
        }
    }

//    @GetMapping("/return")
//    public void returnUrl(HttpServletRequest request,
//                         HttpServletResponse response) throws Exception {
//        Map<String, String> params = convertRequestParametersToMap(request);
//
//        try {
//            // 获取商户配置
//            MultiMerchantAlipayConfig.MerchantConfig merchantConfig =
//                    multiMerchantConfig.getMerchantConfig(merchantId);
//
//            // 验证签名
//            boolean signVerified = AlipaySignature.rsaCheckV1(
//                    params,
//                    merchantConfig.getPublicKey(),
//                    merchantConfig.getCharset(),
//                    merchantConfig.getSignType()
//            );
//
//            if (!signVerified) {
//                response.sendRedirect(merchantConfig.getReturnUrl() + "?status=failure");
//                return;
//            }
//
//            String outTradeNo = params.get("out_trade_no");
//            // 重定向到商户指定的页面
//            response.sendRedirect(merchantConfig.getReturnUrl() + "?status=success&orderNo=" + outTradeNo);
//
//        } catch (Exception e) {
//            log.error("处理支付宝同步返回异常", e);
//            response.sendRedirect(multiMerchantConfig.getMerchantConfig(merchantId).getReturnUrl()
//                    + "?status=failure");
//        }
//    }

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
            ReturnResponse returnResponse = new ReturnResponse(request.getTradeNo(), "2", OrderStatus.PAID.name(),
                    orderDetailById.getPayAmount(), orderDetailById.getOrderTime());

            return Response.success(returnResponse);
        }
        // 处理支付成功后的逻辑
        ReturnResponse successResponse = new ReturnResponse(request.getTradeNo(), "2", OrderStatus.WAITING_PAYMENT.name(),
                orderDetailById.getPayAmount(), orderDetailById.getOrderTime());
        return Response.success(successResponse);
    }

    @PostMapping("/query")
    public Response<ReturnResponse> queryOrder(@RequestBody ReturnRequest request) throws JsonProcessingException {
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
                new GetOrderDetailByTradeNo(request.getTradeNo()));
        if (Objects.isNull(orderDetailById)) {
            return Response.failure("订单不存在");
        }

        // 返回订单状态
        ReturnResponse returnResponse = new ReturnResponse(
                request.getTradeNo(),
                orderDetailById.getIsPaySuccess() == 2 ? "1" : "0",
                orderDetailById.getIsPaySuccess() == 2 ?
                        OrderStatus.PAID.name() : OrderStatus.WAITING_PAYMENT.name(),
                orderDetailById.getPayAmount(),
                orderDetailById.getOrderTime()
        );

        return Response.success(returnResponse);
    }

    private boolean processOrder(String outTradeNo, String tradeNo, int amount) throws JsonProcessingException {
        // 查询订单是否存在
        GoodsHistoryDO orderDetail = orderService.queryOrderByTradeNo(outTradeNo);
        if (Objects.isNull(orderDetail)) {
            log.error("订单不存在: {}", outTradeNo);
            return false;
        }

        // 验证订单金额（实际应用中应该验证金额是否匹配）
        // TODO: 验证订单金额

        // 更新订单状态
        orderService.updateOrderStatus(outTradeNo, OrderStatus.WAITING_SHIPMENT);

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

    private Map<String, String> convertRequestParametersToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }
}
