package com.wx.common.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

public class AlipayConfigSX {

    // 应用ID
    public static final String APP_ID = "2021000148682771";

    // 应用私钥
    public static final String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDlQwZq0pcOPVkoYuJHWmxT9EZ58ucpOeCwPMvKioJPbSah895V/9hLcLcfTk4u3KpVxYkw5cZiGRu2SHnIF0toAsDrHIjGLyvCAk+qVbVbhzKglwm9730FaKSXJnUzmjnC0OHhL+L/Q8hjDAKmVtLE3fdWBpzh8IBUMHT5gt77t3ozXE9UCuo83RV+HTcGe89USnBUKghWjM/ENbOnmu3rxv9/gTgwWFKLuV9kkFEN88DBTR+FbnWfdLVJLjv5RCC+rQzUPm3+sEB9rcSsH/ZGd0m2MtmhraqhV7Ac1EODr0rluvCSv4EJQbAUTlqYo9RN8plM1kh9sdOjnDvlrW6VAgMBAAECggEAE0kdHBVVAayleGFBGbGAlVtiJlYJng0xWoHyvroCFj5fVpZWKLPZ9RapQOfsacLETWzNiqB5nLzx+NaF8BANltVMdzqyQsGrE6nwG6sUE3v/BUVPW3vu72qLFe/1Qf8CRuIdbj0CAmkoTTSwcF+zYT4u1Ty/K754fRyKaobYzSHMUZ420ouUVQeKqi3XlyDydJNJp+T5gOuCR0dh4A/FUKTq2oO+Y62abVqVHKZLggNukR/l5vIA+ZHswR6e59ju11+4QjEUON17uCr9Af2S4HhFtLYtgw9AjBh2oPzRha7N/fNPw5I077GoDk+KHpOFIixO9zqSHALFgVfO0qXgjQKBgQD1bfhnTmcPBim+Ud/O1zSL8lADumQt6Y6TkWffn5QsHPZfclOrg0q8evJll/71GQFGqy+2vPIqgi04RD+1P0Ti4cXFbIE7Dbl9DaJh2X8K2i7/2DO1lnhNhW9gaMJwCWrJhTbq3yp+CVaF7yWRWKe9Mj2GbXUQ/7v92l2sPt1h/wKBgQDvIstMVZVrpZhIX29WB5jqnq59uhv5FAWQEjjPFquKNH+SYRsedujN4hvikgo5WRC53Po1tg4PC97aief6SIQECUUYmicNnAv1F/gzLkQCCP71Wt4dWJTOrLGHrxSKyE+SegqPS8DSzdVZ+bbcASyJQuzKRMU5OsX7fJCDL4iHawKBgHdlGjTXdVdfviryFZjAJLX4GWjW4ilbt5qeYBPvw9t583kXWxDS/CaZARbfTI7brT09xiZ14LFBRtJakUuRDef5wYZ7dJrP4G6vlVFYx272GWspOG6cVsYPpdEW5ZAvZJ3IiqDUFA3gaREa+AGvSVrG+BtPYGX2ovOpqWxD4NrtAoGBAKU/DurdzRYI5RVF1WLGdzXRl1RMEd5ppnprDRSVXs5u/qTBM4M2uiLjF0/WiPgeAr/c4o3REx/0nPe5CjNaftdnLHcWttvhtCWWfa2oT9h0LPKQ6yGuP44dqcPQCUrpporaiIeC2mkRUVgOhfy7VvI26/fN09d7NKpW5mmwpBCnAoGADavCYcwDiUpreKHO8ULV5BPs4OGrmO1VnGinJg0iOAHcdnSt093iGsAGOce8yMW4aycu+9gG8M8JH/KigqkP8T3MWS7nzhFDvvIiSbwmT6Cl2DOAxYiER6WI0wb4BkMpG4VDrfspoHFjJRpzxp5S8BSFemT0XtYQu3GIZWwv9OA=";

    // 支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjNqLOrDPm4F86ESty1jEUt5KKMKTDnQyjFYguBeP6SJORwPNdmQ92beMVUF6RM59nO4B8SSpv0y3NLoj5ceu6FjtezfRxc9CZ2iSefYaZHsMiQDux2vd92QIzWQGb+rGHPNGvM9IKs/aN1G3mqBNsUSdAA2S0tUHbx1GK3pF3kI3ZbyQ2dfbDGp1BHXgJktlWTfvRis0XmsQ4wMJyv9ovi6eg/vrTjeqlqedhZJI3aBjfqhlsYAt7PMoRNsOWpUv0G5d7Eu6xJpoehxOzLOlTsBhVwOXZs3tvkFznU3DAq9gcsK9ZYElLIc4FC28gm3WOXqD3eSUeAgXzVobc9/g1QIDAQAB";
    // MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvcKjVG3uK7n2dDjDq3jP4XoW3HByLPLMXJGnkLNSmhzft/3OZw7Q7dgRvk/SahoudBn97myc1+utjuSBIW0/sPplo2OrP3CUml4LtU6o9VFfAzOunZ1bwyH1adRkWHO24qT5jNnwGJoByCRunk1rK/qqc1ML7CGQPl9E3lCRyVQWI8qhpY64YbuIzSLXwVf/HuucxmFnS9J5Z7gk3qdylDJz9p8tk4EUkolKb27yX5SCuNegcEMO1IHfongdyoMQSkpKZLdCiz46/Dn0/hQ8uvR3SU2X4gQhX0UEd7ZCcxuqKWVquplwvf7pSeygudQle5A8UX5mUljZbx1xWY4F0wIDAQAB
    // 服务器异步通知页面路径
    public static final String NOTIFY_URL = "http://g8a65b28.natappfree.cc/alipay/notify";

    // 页面跳转同步通知页面路径
    public static final String RETURN_URL = "http://g8a65b28.natappfree.cc/alipay/return";

    // 签名方式
    public static final String SIGN_TYPE = "RSA2";

    // 字符编码格式
    public static final String CHARSET = "UTF-8";

    // 支付宝网关
//    public static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
    public static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    // 实例化客户端
    public static AlipayClient alipayClient = new DefaultAlipayClient(
            GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
}