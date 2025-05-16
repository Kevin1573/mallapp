package com.wx.controller;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.google.zxing.WriterException;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wx.common.utils.Constants;
import com.wx.common.utils.QrCodeUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pay/v3")
@AllArgsConstructor
public class WxPayController {
    private NativePayService payService;
    private NotificationParser notificationParser;

    @PostMapping("/createOrderNative")
    public String createOrderNative(@RequestBody Map<String, String> params) throws WxPayException, IOException, WriterException {
        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(Integer.valueOf(params.get("amountTotal")));
        request.setAmount(amount);
        request.setAppid(Constants.APP_ID);
        request.setMchid(Constants.MERCHANT_ID);
        request.setDescription(params.getOrDefault("description", ""));
        request.setNotifyUrl(Constants.CALLBACK_URL);
        String orderId = "ORDER_" + System.currentTimeMillis();
        request.setOutTradeNo(orderId);
        Map<String, String> attachMap = new HashMap<>();
        attachMap.put("orderId", orderId);
        attachMap.put("amountTotal", params.get("amountTotal"));
        request.setAttach(JSON.toJSONString(attachMap));
        try {
            PrepayResponse prepay = payService.prepay(request);
        } catch (Exception e) {
            String message = e.getMessage();
            // 正则表达式：匹配 Wechatpay-Serial 后面的值
            Pattern pattern = Pattern.compile("Wechatpay-Serial=([^,]*)");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String serialValue = matcher.group(1);
                System.out.println("Wechatpay-Serial value: " + serialValue);
                if (!Constants.PLATFORM_KEY.equals(serialValue)) {
                    return "平台密钥错误";
                }
            } else {
                System.out.println("Wechatpay-Serial not found");
                return "error";
            }

            // 正则表达式匹配 code_url 的值
            Pattern pattern2 = Pattern.compile("\"code_url\"\\s*:\\s*\"([^\"]+)\"");
            Matcher matcher2 = pattern2.matcher(message);
            if (matcher2.find()) {
                String codeUrl = matcher2.group(1);
                String base64Image = QrCodeUtil.generateQrCodeBase64(codeUrl, 300);
                System.out.println("Data URI: data:image/png;base64," + base64Image);

                return base64Image;
            } else {
                System.out.println("code_url not found");
            }
            return message;
        }
        return null;
    }

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public String callback(HttpServletRequest request) {
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
            return processTransaction(transaction);

        } catch (Exception e) {
            // 记录错误日志
            return buildErrorResponse("FAIL", "处理失败: " + e.getMessage());
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

        try {
            // TODO: 实现你的业务逻辑
            // 注意：必须做幂等处理，防止重复通知
            boolean success = processOrder(outTradeNo, transactionId, amount);

            return success ? buildSuccessResponse() : buildErrorResponse("FAIL", "订单处理失败");
        } catch (Exception e) {
            return buildErrorResponse("FAIL", "系统异常: " + e.getMessage());
        }
    }


    private boolean processOrder(String outTradeNo, String transactionId, int amount) {
        // 实现你的业务逻辑，例如：
        // 1. 检查订单是否存在
        // 2. 验证金额是否匹配
        // 3. 更新订单状态
        // 4. 记录支付信息
        // 返回处理结果
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
