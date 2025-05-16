package com.wx.service;

import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wx.common.utils.Constants;
import org.springframework.stereotype.Service;

import static com.wx.common.utils.Constants.APP_ID;

@Service
public class WxNativePayService {

    private final NativePayService nativePayService;

    public WxNativePayService(NativePayService nativePayService) {
        this.nativePayService = nativePayService;
    }

    /**
     * 创建Native支付订单
     * @param outTradeNo 商户订单号
     * @param amount 金额(分)
     * @param description 商品描述
     * @return 支付二维码链接
     */
    public String createNativeOrder(String outTradeNo, int amount, String description) {
        // 1. 构建请求参数
        PrepayRequest request = new PrepayRequest();
        Amount amountObj = new Amount();
        amountObj.setTotal(amount);
        request.setAmount(amountObj);
        request.setAppid(APP_ID);
        request.setMchid(Constants.MERCHANT_ID);
        request.setDescription(description);
        request.setNotifyUrl(Constants.CALLBACK_URL);
        request.setOutTradeNo(outTradeNo);
        
        // 2. 调用下单接口
        PrepayResponse response = nativePayService.prepay(request);
        
        // 3. 返回支付二维码链接
        return response.getCodeUrl();
    }
}