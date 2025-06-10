//package com.wx.controller;
//
//import com.wechat.pay.java.core.notification.NotificationParser;
//import com.wechat.pay.java.service.payments.model.Transaction;
//import com.wx.common.config.MultiMerchantWxPayConfig;
//import com.wx.common.enums.OrderStatus;
//import com.wx.common.enums.PayWayEnums;
//import com.wx.common.util.WxPayCipherUtil;
//import com.wx.service.OrderService;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@RestController
//@RequestMapping("/pay/v3/wx/callback")
//@AllArgsConstructor
//public class WxPayCallbackController {
//
//    private final MultiMerchantWxPayConfig wxPayConfig;
//    private final OrderService orderService;
//    private final StockService stockService;
//
//    /**
//     * 支付结果回调通知
//     */
//    @PostMapping("/{merchantId}")
//    public Map<String, String> parseNotification(@PathVariable String merchantId,
//                                               HttpServletRequest request) {
//        log.info("收到微信支付回调通知, merchantId: {}", merchantId);
//        Map<String, String> result = new HashMap<>();
//
//        try {
//            // 1. 获取请求头和请求体
//            String timestamp = request.getHeader("Wechatpay-Timestamp");
//            String nonce = request.getHeader("Wechatpay-Nonce");
//            String signature = request.getHeader("Wechatpay-Signature");
//            String signatureType = request.getHeader("Wechatpay-Signature-Type");
//            String serialNumber = request.getHeader("Wechatpay-Serial");
//
//            // 读取请求体
//            String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//            log.info("微信支付回调原始数据: {}", body);
//
//            // 2. 构造通知解析器
//            NotificationParser parser = new NotificationParser(wxPayConfig.getConfig(merchantId));
//
//            // 3. 验签和解析请求体
//            Transaction transaction = parser.parse(body, signature, nonce, timestamp);
//
//            // 4. 处理支付结果
//            if ("SUCCESS".equals(transaction.getTradeState().toString())) {
//                String tradeNo = transaction.getOutTradeNo();
//                log.info("微信支付成功，订单号: {}", tradeNo);
//
//                try {
//                    // 更新订单支付方式
//                    orderService.updatePayway(tradeNo, PayWayEnums.WECHAT.name());
//
//                    // 处理支付成功后的业务逻辑（包括库存扣减）
//                    boolean processed = orderService.processPaymentSuccess(tradeNo);
//
//                    if (processed) {
//                        log.info("订单处理成功，订单号: {}", tradeNo);
//                        result.put("code", "SUCCESS");
//                        result.put("message", "成功");
//                    } else {
//                        log.error("订单处理失败，订单号: {}", tradeNo);
//                        result.put("code", "FAIL");
//                        result.put("message", "订单处理失败");
//                    }
//                } catch (Exception e) {
//                    log.error("处理支付成功业务逻辑时发生错误，订单号: " + tradeNo, e);
//                    // 发生异常时，不要立即返回失败
//                    // 微信会重试回调，我们后续可以通过补偿机制处理
//                    result.put("code", "SUCCESS");
//                    result.put("message", "成功");
//                }
//            } else {
//                log.warn("支付未成功，状态: {}, 订单号: {}",
//                        transaction.getTradeState(),
//                        transaction.getOutTradeNo());
//                result.put("code", "SUCCESS");
//                result.put("message", "成功");
//            }
//        } catch (Exception e) {
//            log.error("处理微信支付回调通知时发生错误", e);
//            result.put("code", "FAIL");
//            result.put("message", "系统错误");
//        }
//
//        return result;
//    }
//
//    /**
//     * 退款回调通知
//     */
//    @PostMapping("/refund/{merchantId}")
//    public Map<String, String> parseRefundNotification(@PathVariable String merchantId,
//                                                     HttpServletRequest request) {
//        log.info("收到微信支付退款回调通知, merchantId: {}", merchantId);
//        Map<String, String> result = new HashMap<>();
//
//        try {
//            // 1. 获取请求头和请求体
//            String timestamp = request.getHeader("Wechatpay-Timestamp");
//            String nonce = request.getHeader("Wechatpay-Nonce");
//            String signature = request.getHeader("Wechatpay-Signature");
//            String serialNumber = request.getHeader("Wechatpay-Serial");
//
//            // 读取请求体
//            String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//            log.info("微信支付退款回调原始数据: {}", body);
//
//            // 2. 构造通知解析器并验证签名
//            NotificationParser parser = new NotificationParser(wxPayConfig.getConfig(merchantId));
//
//            // 3. 解析通知数据
//            com.wechat.pay.java.service.refund.model.RefundNotification notification =
//                parser.parse(body, signature, nonce, timestamp);
//
//            // 4. 解密退款通知数据
//            String ciphertext = notification.getre().getCiphertext();
//            String associatedData = "refund";
//            String apiV3Key = wxPayConfig.getConfig(merchantId).getApiV3Key();
//
//            String decryptedData = WxPayCipherUtil.decryptToString(
//                apiV3Key,
//                associatedData,
//                notification.getResource().getNonce(),
//                ciphertext
//            );
//
//            log.info("解密后的退款通知数据: {}", decryptedData);
//
//            // 5. 处理退款结果
//            if ("SUCCESS".equals(notification.getRefund().getRefundStatus())) {
//                String orderId = notification.getRefund().getOutTradeNo();
//
//                // 更新订单状态为已退款
//                orderService.updateOrderStatus(orderId, OrderStatus.REFUNDED);
//
//                // 恢复商品库存
//                var order = orderService.queryOrderByTradeNo(orderId);
//                if (order != null) {
//                    Map<Long, Integer> items = new HashMap<>();
//                    items.put(order.getGoodsId(), order.getNum());
//                    stockService.increaseStockBatch(items);
//                }
//
//                log.info("退款成功处理完成，订单号: {}", orderId);
//                result.put("code", "SUCCESS");
//                result.put("message", "成功");
//            } else {
//                log.warn("退款未成功，状态: {}, 订单号: {}",
//                    notification.getRefund().getRefundStatus(),
//                    notification.getRefund().getOutTradeNo());
//                result.put("code", "SUCCESS");
//                result.put("message", "成功");
//            }
//        } catch (Exception e) {
//            log.error("处理微信支付退款回调通知时发生错误", e);
//            result.put("code", "FAIL");
//            result.put("message", "系统错误");
//        }
//
//        return result;
//    }
//}
