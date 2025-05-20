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

    private static AlipayConfig getAlipayConfig() {
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDlQwZq0pcOPVkoYuJHWmxT9EZ58ucpOeCwPMvKioJPbSah895V/9hLcLcfTk4u3KpVxYkw5cZiGRu2SHnIF0toAsDrHIjGLyvCAk+qVbVbhzKglwm9730FaKSXJnUzmjnC0OHhL+L/Q8hjDAKmVtLE3fdWBpzh8IBUMHT5gt77t3ozXE9UCuo83RV+HTcGe89USnBUKghWjM/ENbOnmu3rxv9/gTgwWFKLuV9kkFEN88DBTR+FbnWfdLVJLjv5RCC+rQzUPm3+sEB9rcSsH/ZGd0m2MtmhraqhV7Ac1EODr0rluvCSv4EJQbAUTlqYo9RN8plM1kh9sdOjnDvlrW6VAgMBAAECggEAE0kdHBVVAayleGFBGbGAlVtiJlYJng0xWoHyvroCFj5fVpZWKLPZ9RapQOfsacLETWzNiqB5nLzx+NaF8BANltVMdzqyQsGrE6nwG6sUE3v/BUVPW3vu72qLFe/1Qf8CRuIdbj0CAmkoTTSwcF+zYT4u1Ty/K754fRyKaobYzSHMUZ420ouUVQeKqi3XlyDydJNJp+T5gOuCR0dh4A/FUKTq2oO+Y62abVqVHKZLggNukR/l5vIA+ZHswR6e59ju11+4QjEUON17uCr9Af2S4HhFtLYtgw9AjBh2oPzRha7N/fNPw5I077GoDk+KHpOFIixO9zqSHALFgVfO0qXgjQKBgQD1bfhnTmcPBim+Ud/O1zSL8lADumQt6Y6TkWffn5QsHPZfclOrg0q8evJll/71GQFGqy+2vPIqgi04RD+1P0Ti4cXFbIE7Dbl9DaJh2X8K2i7/2DO1lnhNhW9gaMJwCWrJhTbq3yp+CVaF7yWRWKe9Mj2GbXUQ/7v92l2sPt1h/wKBgQDvIstMVZVrpZhIX29WB5jqnq59uhv5FAWQEjjPFquKNH+SYRsedujN4hvikgo5WRC53Po1tg4PC97aief6SIQECUUYmicNnAv1F/gzLkQCCP71Wt4dWJTOrLGHrxSKyE+SegqPS8DSzdVZ+bbcASyJQuzKRMU5OsX7fJCDL4iHawKBgHdlGjTXdVdfviryFZjAJLX4GWjW4ilbt5qeYBPvw9t583kXWxDS/CaZARbfTI7brT09xiZ14LFBRtJakUuRDef5wYZ7dJrP4G6vlVFYx272GWspOG6cVsYPpdEW5ZAvZJ3IiqDUFA3gaREa+AGvSVrG+BtPYGX2ovOpqWxD4NrtAoGBAKU/DurdzRYI5RVF1WLGdzXRl1RMEd5ppnprDRSVXs5u/qTBM4M2uiLjF0/WiPgeAr/c4o3REx/0nPe5CjNaftdnLHcWttvhtCWWfa2oT9h0LPKQ6yGuP44dqcPQCUrpporaiIeC2mkRUVgOhfy7VvI26/fN09d7NKpW5mmwpBCnAoGADavCYcwDiUpreKHO8ULV5BPs4OGrmO1VnGinJg0iOAHcdnSt093iGsAGOce8yMW4aycu+9gG8M8JH/KigqkP8T3MWS7nzhFDvvIiSbwmT6Cl2DOAxYiER6WI0wb4BkMpG4VDrfspoHFjJRpzxp5S8BSFemT0XtYQu3GIZWwv9OA="; //"<-- 请填写您的应用私钥，例如：MIIEvQIBADANB ... ... -->";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjNqLOrDPm4F86ESty1jEUt5KKMKTDnQyjFYguBeP6SJORwPNdmQ92beMVUF6RM59nO4B8SSpv0y3NLoj5ceu6FjtezfRxc9CZ2iSefYaZHsMiQDux2vd92QIzWQGb+rGHPNGvM9IKs/aN1G3mqBNsUSdAA2S0tUHbx1GK3pF3kI3ZbyQ2dfbDGp1BHXgJktlWTfvRis0XmsQ4wMJyv9ovi6eg/vrTjeqlqedhZJI3aBjfqhlsYAt7PMoRNsOWpUv0G5d7Eu6xJpoehxOzLOlTsBhVwOXZs3tvkFznU3DAq9gcsK9ZYElLIc4FC28gm3WOXqD3eSUeAgXzVobc9/g1QIDAQAB"; //"<-- 请填写您的支付宝公钥，例如：MIIBIjANBg... -->";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do"); // "https://openapi.alipay.com/gateway.do"
        alipayConfig.setAppId("2021000148682771"); //"<-- 请填写您的AppId，例如：2019091767145019 -->"
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
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
}