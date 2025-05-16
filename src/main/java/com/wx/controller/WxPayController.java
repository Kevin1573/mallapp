package com.wx.controller;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.google.zxing.WriterException;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wx.common.utils.Constants;
import com.wx.common.utils.QrCodeUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/pay/v3")
@AllArgsConstructor
public class WxPayController {
    private NativePayService payService;

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
        request.setOutTradeNo("ORDER_" + System.currentTimeMillis());
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

}
