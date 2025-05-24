package com.wx.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.BusinessParams;
import com.alipay.api.domain.ExtendParams;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.aliyuncs.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wx.common.config.AlipayConfigConf;
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
import java.util.*;

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

    @GetMapping("/pay2")
    public String pay2(PaymentRequest request) throws AlipayApiException {
        // 生成商户订单号
        String outTradeNo = OrderUtil.snowflakeOrderNo();
        // 支付金额
        String totalAmount = "0.01";
        String defaultSubject = "Iphone6 16G";
        // 调用支付
        return AlipayService.createQrPayment(outTradeNo, totalAmount,
                StringUtils.isEmpty(request.getSubject()) ? defaultSubject : request.getSubject());
    }

    @GetMapping("/tradePrecreate")
    public String tradePrecreate() throws AlipayApiException {
        // 生成商户订单号
        String outTradeNo = OrderUtil.snowflakeOrderNo();
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();

        // 设置商户订单号
        model.setOutTradeNo(outTradeNo);

        // 设置订单总金额
        model.setTotalAmount("88.88");

        // 设置订单标题
        model.setSubject("Iphone6 16G");

        // 设置产品码
        model.setProductCode("QR_CODE_OFFLINE");

        // 设置卖家支付宝用户ID
        model.setSellerId("2088721067866087");

        // 设置订单附加信息
        model.setBody("Iphone6 16G");

        // 设置订单包含的商品列表信息
        List<GoodsDetail> goodsDetail = new ArrayList<>();
        GoodsDetail goodsDetail0 = new GoodsDetail();
        goodsDetail0.setGoodsName("ipad");
        goodsDetail0.setQuantity(1L);
        goodsDetail0.setPrice("2000");
        goodsDetail0.setGoodsId("apple-01");
        goodsDetail0.setGoodsCategory("34543238");
        goodsDetail0.setCategoriesTree("124868003|126232002|126252004");
        goodsDetail0.setShowUrl("http://www.alipay.com/xxx.jpg");
        goodsDetail.add(goodsDetail0);
        model.setGoodsDetail(goodsDetail);

        // 设置业务扩展参数
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088511833207846");
        model.setExtendParams(extendParams);

        // 设置商户传入业务信息
        BusinessParams businessParams = new BusinessParams();
        businessParams.setMcCreateTradeIp("127.0.0.1");
        model.setBusinessParams(businessParams);

        // 设置可打折金额
        model.setDiscountableAmount("80.00");

        // 设置商户门店编号
        model.setStoreId("NJ_001");

        // 设置商户操作员编号
        model.setOperatorId("yx_001");

        // 设置商户机具终端编号
        model.setTerminalId("NJ_T_001");

        // 设置商户的原始订单号
        model.setMerchantOrderNo("20161008001");

        request.setBizModel(model);
        // 第三方代调用模式下请设置app_auth_token
        // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");
        // 调用支付
        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        System.out.println(response.getBody());

        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            // String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            // System.out.println(diagnosisUrl);
        }


        return response.getBody();
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

    // 页面跳转同步通知
    @PostMapping("/payReturn")
    public String payReturn(HttpServletRequest request) {
        Map<String, String> params = convertRequestParamsToMap(request);
        return "payReturn -> " + JSON.toJSONString(params);
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
                    AlipayConfigConf.ALIPAY_PUBLIC_KEY,
                    AlipayConfigConf.CHARSET,
                    AlipayConfigConf.SIGN_TYPE);

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



    public static void main(String[] args) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();

        // 设置商户订单号
        model.setOutTradeNo("20150320010101001");

        // 设置订单总金额
        model.setTotalAmount("88.88");

        // 设置订单标题
        model.setSubject("Iphone6 16G");

        // 设置产品码
        model.setProductCode("QR_CODE_OFFLINE");

        // 设置卖家支付宝用户ID
        model.setSellerId("2088102146225135");

        // 设置订单附加信息
        model.setBody("Iphone6 16G");

        // 设置订单包含的商品列表信息
        List<GoodsDetail> goodsDetail = new ArrayList<GoodsDetail>();
        GoodsDetail goodsDetail0 = new GoodsDetail();
        goodsDetail0.setGoodsName("ipad");
        goodsDetail0.setQuantity(1L);
        goodsDetail0.setPrice("2000");
        goodsDetail0.setGoodsId("apple-01");
        goodsDetail0.setGoodsCategory("34543238");
        goodsDetail0.setCategoriesTree("124868003|126232002|126252004");
        goodsDetail0.setShowUrl("http://www.alipay.com/xxx.jpg");
        goodsDetail.add(goodsDetail0);
        model.setGoodsDetail(goodsDetail);

        // 设置业务扩展参数
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088511833207846");
        model.setExtendParams(extendParams);

        // 设置商户传入业务信息
        BusinessParams businessParams = new BusinessParams();
        businessParams.setMcCreateTradeIp("127.0.0.1");
        model.setBusinessParams(businessParams);

        // 设置可打折金额
        model.setDiscountableAmount("80.00");

        // 设置商户门店编号
        model.setStoreId("NJ_001");

        // 设置商户操作员编号
        model.setOperatorId("yx_001");

        // 设置商户机具终端编号
        model.setTerminalId("NJ_T_001");

        // 设置商户的原始订单号
        model.setMerchantOrderNo("20161008001");

        request.setBizModel(model);
        // 第三方代调用模式下请设置app_auth_token
        // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        System.out.println(response.getBody());

        if (response.isSuccess()) {
            log.info("预创建成功，qr_code：{}", response.getQrCode());
        } else {
            log.error("预创建失败，sub_code：{}，sub_msg：{}",
                    response.getSubCode(),
                    response.getSubMsg());
        }

        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            // String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            // System.out.println(diagnosisUrl);
        }

    }

    private static AlipayConfig getAlipayConfig() {
        String privateKey  = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDJYUev6L3ECS80ZPALNkxh8wj/JpEkc2fezT1VQsfBG3wc0EX9cor4eR17+dc4EdneQkAhf4tao2iijDxXKmZjG4kjTMu4ggRjsnSpeCZW+r8CWzC8IOOm+clHCDUcghXRYmDYdN5eLcMLXV2d6EQ/M1+3H2ne1cyZA5qEhb/rPEL1Kq76XukhgEFjHL0Xphs0SB/DC9HpTI1jyNsuO43fQCpqsUF08ViwUCX3LLqUtOq+Pk2px0yh31Pmc2j7FT77mCLC6Bx/tSH3BtxCHMPuvVfjVe1cuMlQAg6pp2hQTcDLqLm5dRTZj/QclyCfbF+tkXiKGiQTz5/mHvmjswgFAgMBAAECggEAKJ86+r0UKos/vm3uDhTx64A+/FknRhcRiNwV1zEVYlrM+nL461tDtUOZMvz+8QyIylDK5vb3gV0dKkznjx26cZuIWlqPbbSsdf/1kInwEfOavDrw5cIsqe4RMAbzz8Bd7lLN/lv0z0Kj6ZL343aK7cTLNgFcNdsi3mrfrIPeZvDxWKSLjSEqPScxYhxFeDyApgWcxJ8oe5DQTxOHNvI1JlCdY/KrbyV2XqyBR2kERuHb3TLFQ+tQ07ggtICbyisBKLMS0YAyuVISsz2pmatfluLtXE4TgY5xaimQh62b0FETvPQNuPvez+LQy78ZN9DWFKD1f3dERpj2g7LnmC2lMQKBgQDq+SFQR/zrkDpyUGifEMbns066ZjkPWoK6dGx/fvJKFxPv1WTT3ghkiq7PUvZCQ9v+4nLpR4YUaxkhLChUiFsbczd+lw4UY12jlO8JFLdQNkVfYsd4+DevQ8mVLCNTDnc1pe9CYdICUWsBBiCzHC4P3ThnG8MquF3g2TrB0/LPkwKBgQDbZpUtte6W97jwvgJvlKj2kpdHk0comF48lyOJaipvOdj3fkrQPH+oi4KCy1y9kMlgX89fTANQ3UqQ+3zhRZea+rOby29d+4+6Y1NL8qn6dFV/N5n/5vvkqsw2iswhEi8T0nXytqXJkhrd5FlcuagpIj1zo9lDmoyGfi+FcU4ZBwKBgQDn6x87auIy6vcVD1JD9b4swOjqx6x4AADZ4cGZEYY5JJAT/w52o0arXcpubVcMTogcCgwbPfITwyVZfYkM7kzmShEzDArkirLIm15XGzBXpklQfWGef3gOsByN9LOk7bkxpWcCoSQ0D4JKz26E+kJofBOIiXlXeAOIwz/gQb79qwKBgAhK10aUAsGH+WB3/gWTm9M99SPKaD0bjSxDFh/CeHoduJqaFl/KeZS4OSWSZB3yE/plhKP4boOm6YOTTcQB6ln8Mb+or7vgny0PRf2v3UbPENAvHM30P/8DiZZiJpb1Zfwcz0JuLtPwhr1uPQZpKfbsCWCOh86rT7ZhnJAxmR8PAoGAD4A3sukE+AuJz6/BgMjwZQZT0IaxTg+KcRLQyWmQ2xj+t8aNUwUIKLNcJR3Of2jSf0Gdval0rdnXfK1YXFX+473QhJKZAYWbFt1JzF9GNYa4K/J2wU4oDlAGIj0epV8k5JHg5Lj4FtgrqaTEJFNaZid219Xd8cX4afeauxMoWcY=";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvcKjVG3uK7n2dDjDq3jP4XoW3HByLPLMXJGnkLNSmhzft/3OZw7Q7dgRvk/SahoudBn97myc1+utjuSBIW0/sPplo2OrP3CUml4LtU6o9VFfAzOunZ1bwyH1adRkWHO24qT5jNnwGJoByCRunk1rK/qqc1ML7CGQPl9E3lCRyVQWI8qhpY64YbuIzSLXwVf/HuucxmFnS9J5Z7gk3qdylDJz9p8tk4EUkolKb27yX5SCuNegcEMO1IHfongdyoMQSkpKZLdCiz46/Dn0/hQ8uvR3SU2X4gQhX0UEd7ZCcxuqKWVquplwvf7pSeygudQle5A8UX5mUljZbx1xWY4F0wIDAQAB";
        AlipayConfig alipayConfig = new AlipayConfig();
//        alipayConfig.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do");
//        alipayConfig.setAppId("2021000148682771");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
//        alipayConfig.setSignType("RSA2");
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do"); // ✅ 沙箱地址正确
        alipayConfig.setAppId("2021005142672288"); // ✅ 需确认开放平台应用状态是否正常
        alipayConfig.setSignType("RSA2"); // ✅ 必须与密钥类型匹配

        return alipayConfig;
    }

//    public static void main(String[] args) throws Exception {
//        String privateKey = "你的私钥";
//        String publicKey = "你的公钥";
//
//        // 测试签名 & 验签
//        String content = "test";
//        Signature signature = Signature.getInstance("SHA256WithRSA");
//        signature.initSign(PrivateKeyFactory.fromEncodedPem(privateKey));
//        signature.update(content.getBytes());
//        byte[] signed = signature.sign();
//
//        signature.initVerify(PublicKeyFactory.fromEncodedPem(publicKey));
//        signature.update(content.getBytes());
//        System.out.println("验签结果：" + signature.verify(signed)); // 必须输出 true
//    }
}