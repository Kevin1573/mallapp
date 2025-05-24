package com.wx.common.config;

public class PayConstants {

    public static final String PUBLIC_KEY_ID = "PUB_KEY_ID_0117152438052025051500351948001800";
//    public static final String PUBLIC_KEY_PATH = "-----BEGIN PUBLIC KEY-----\n"
//            + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlrAd64NNyL21FPvCxmMB\n"
//            + "d+XOIYMW0JccfRbjrMscKikYe369pLhnBIOesJ4GuynzjPYZAb1bOVUofc8I+Ug+\n"
//            + "tFZHax8BdhY8uVL71BCC2Fp4jwgCoVjluvOgP/pt58l/Uni40FNvISn5lVvoGd82\n"
//            + "rgZCeRa29UBcpNuF++cmKVtWWK8mSbQP5Gj1AYrwihpASqO3qhv/F3eZCICFpQtS\n"
//            + "oB+FaQckBt91Qv7kRAuVq2CAQz7Ik9S6PZyoeakSNRudpbZOcYPEf7VWBq4W3AZy\n"
//            + "TxsMW0LdNauBFtyaDguEBF3jWGSV9AsDbKDMG3fkIC/8LumIvKLN5Uz1OHiePpsB\n"
//            + "PQIDAQAB\n"
//            + "-----END PUBLIC KEY-----";

    public static final String LOCAL_PUBLIC_KEY_PATH = "D:\\code\\mallapp\\src\\main\\resources\\wxCert\\pub_key.pem";
    public static final String PUBLIC_KEY_PATH = "/root/mallapp/wxCert/pub_key.pem";

    /**
     * 商户号
     */
    public static String MERCHANT_ID = "1715243805";

    public static String PLATFORM_KEY = "68EE48914BD37392C6E2320FD07DAD71FD61F69F";

    /**
     * 商户API私钥路径
     */
//    public static String PRIVATE_KEY_PATH = "/Users/gzj/1657722877_20231101_cert/apiclient_key.pem";
    public static String PRIVATE_KEY_PATH_LOCAL = "D:\\code\\mallapp\\src\\main\\resources\\wxCert\\apiclient_key.pem";
    public static String PRIVATE_KEY_PATH = "/root/mallapp/wxCert/apiclient_key.pem";

    /**
     * 商户证书序列号
     */
    public static String MERCHANT_SERIAL_NUMBER = "4145FC26AA94354CF3DB18870420E0E80AED7A82";

    /**
     * 商户APIV3密钥
     */
    public static String API_V3_KEY = "7Uhb6ygv5tfc4rdx3esz2wa1q0pl9okm";

    public static String PRIVATE_KEY = "68EE48914BD37392C6E2320FD07DAD71FD61F69F";

    public static String CALLBACK_URL = "http://mallapp.pdcspace.com/pay/v3/callback";
    public static String LOCAL_CALLBACK_URL = "http://g8a65b28.natappfree.cc/pay/v3/callback";


    public static String APP_ID = "wx70559cc67cdc94cc";

    public static String APP_SECRET = "";

    public static String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    public static final String ACCESS_KEY_ID = "";

    public static final String ACCESS_KEY_SECRET = "";

    public static final String ROLE_ARN = "acs:ram::1275758408595557:role/aliyunoss";

    public static final String ROLE_SESSION_NAME = "adminSession";

    public static final String ENDPOINT = "sts.cn-hangzhou.aliyuncs.com";

    public static final String REGION = "oss-cn-hangzhou";


    public static final String DEFAULT_HEAD = "https://0563headurl.oss-cn-hangzhou.aliyuncs.com/default/pic.webp";

}
