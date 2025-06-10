# 微信支付回调处理指南

## 1. 配置说明

### 1.1 API V3密钥配置
在使用微信支付回调功能前，需要先配置商户的API V3密钥。该密钥用于解密回调通知中的敏感信息。

```yaml
# application.yml
wx:
  pay:
    merchants:
      default:  # 默认商户配置
        mchId: your-merchant-id
        apiV3Key: your-api-v3-key
        privateKeyPath: path/to/your/private/key.pem
        merchantSerialNumber: your-merchant-serial-number
        apiV3CertPath: path/to/your/cert.pem
      merchant2:  # 其他商户配置（如果有多个商户）
        mchId: another-merchant-id
        apiV3Key: another-api-v3-key
        # ...其他配置项
```

### 1.2 回调接口配置
在微信支付商户平台配置以下回调地址：

- 支付结果通知：`https://your-domain.com/pay/v3/wx/callback/{merchantId}`
- 退款结果通知：`https://your-domain.com/pay/v3/wx/callback/refund/{merchantId}`

## 2. API说明

### 2.1 支付结果回调
处理支付完成后的通知，更新订单状态和进行库存扣减。

```java
@PostMapping("/{merchantId}")
public Map<String, String> parseNotification(@PathVariable String merchantId,
                                           HttpServletRequest request)
```

### 2.2 退款结果回调
处理退款完成后的通知，更新订单状态和恢复商品库存。

```java
@PostMapping("/refund/{merchantId}")
public Map<String, String> parseRefundNotification(@PathVariable String merchantId,
                                                 HttpServletRequest request)
```

### 2.3 解密工具类
用于解密回调通知中的加密数据。

```java
public class WxPayCipherUtil {
    public static String decryptToString(String apiV3Key, 
                                       String associatedData, 
                                       String nonce, 
                                       String ciphertext)
}
```

## 3. 使用示例

### 3.1 解密回调数据

```java
// 获取回调请求中的必要信息
String timestamp = request.getHeader("Wechatpay-Timestamp");
String nonce = request.getHeader("Wechatpay-Nonce");
String signature = request.getHeader("Wechatpay-Signature");
String serialNumber = request.getHeader("Wechatpay-Serial");

// 读取请求体
String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

// 解析通知数据
NotificationParser parser = new NotificationParser(wxPayConfig.getConfig(merchantId));
RefundNotification notification = parser.parse(body, signature, nonce, timestamp);

// 解密退款通知数据
String decryptedData = WxPayCipherUtil.decryptToString(
    apiV3Key,
    "refund",  // associatedData
    notification.getResource().getNonce(),
    notification.getResource().getCiphertext()
);
```

### 3.2 处理退款结果

```java
// 解析解密后的数据
if ("SUCCESS".equals(notification.getRefund().getRefundStatus())) {
    String orderId = notification.getRefund().getOutTradeNo();
    
    // 更新订单状态
    orderService.updateOrderStatus(orderId, OrderStatus.REFUNDED);
    
    // 恢复商品库存
    var order = orderService.queryOrderByTradeNo(orderId);
    if (order != null) {
        Map<Long, Integer> items = new HashMap<>();
        items.put(order.getGoodsId(), order.getNum());
        stockService.increaseStockBatch(items);
    }
}
```

## 4. 常见问题

### 4.1 回调验签失败
- 检查API V3密钥是否正确配置
- 确认请求头中的时间戳是否在合理范围内
- 验证签名算法是否正确实现

### 4.2 解密失败
- 确认使用的是正确的API V3密钥
- 检查associatedData和nonce是否正确
- 验证密文格式是否完整

### 4.3 重复通知处理
微信支付可能会多次发送同一个通知，建议：
- 根据订单号做幂等处理
- 在数据库事务中完成状态更新
- 对于成功处理的通知，返回成功响应以防止重复通知

### 4.4 回调超时
- 回调接口需要在5秒内处理完成并返回结果
- 建议异步处理耗时操作
- 即使处理过程中出现异常，也应该返回成功响应，避免微信支付重复发送通知

## 5. 最佳实践

1. 实现幂等性处理：
```java
@Transactional(rollbackFor = Exception.class)
public boolean processRefund(String orderId) {
    // 检查订单状态，避免重复处理
    if (isOrderRefunded(orderId)) {
        return true;
    }
    
    // 处理退款逻辑
    // ...
}
```

2. 异步处理耗时操作：
```java
@Async
public void processRefundAsync(String orderId) {
    try {
        // 处理耗时操作
        processRefund(orderId);
    } catch (Exception e) {
        // 记录错误日志，后续补偿处理
        log.error("处理退款异步任务失败", e);
    }
}
```

3. 完善的日志记录：
```java
log.info("收到退款回调通知, 订单号: {}, 退款状态: {}", 
    notification.getRefund().getOutTradeNo(),
    notification.getRefund().getRefundStatus());
```

## 6. 注意事项

1. 安全性
   - 必须验证请求签名
   - 敏感信息需要解密处理
   - 不要在日志中记录敏感信息

2. 性能
   - 回调接口要求5秒内返回
   - 耗时操作应该异步处理
   - 考虑使用缓存优化查询

3. 可靠性
   - 实现幂等性处理
   - 使用事务保证数据一致性
   - 添加异常重试机制

4. 监控
   - 记录详细的日志
   - 添加关键指标监控
   - 设置异常告警机制
