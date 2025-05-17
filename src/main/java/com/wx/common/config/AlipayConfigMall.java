package com.wx.common.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

public class AlipayConfigMall {

    // 应用ID
    public static final String APP_ID = "2021005142672288";

    // 应用私钥
    public static final String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDJYUev6L3ECS80ZPALNkxh8wj/JpEkc2fezT1VQsfBG3wc0EX9cor4eR17+dc4EdneQkAhf4tao2iijDxXKmZjG4kjTMu4ggRjsnSpeCZW+r8CWzC8IOOm+clHCDUcghXRYmDYdN5eLcMLXV2d6EQ/M1+3H2ne1cyZA5qEhb/rPEL1Kq76XukhgEFjHL0Xphs0SB/DC9HpTI1jyNsuO43fQCpqsUF08ViwUCX3LLqUtOq+Pk2px0yh31Pmc2j7FT77mCLC6Bx/tSH3BtxCHMPuvVfjVe1cuMlQAg6pp2hQTcDLqLm5dRTZj/QclyCfbF+tkXiKGiQTz5/mHvmjswgFAgMBAAECggEAKJ86+r0UKos/vm3uDhTx64A+/FknRhcRiNwV1zEVYlrM+nL461tDtUOZMvz+8QyIylDK5vb3gV0dKkznjx26cZuIWlqPbbSsdf/1kInwEfOavDrw5cIsqe4RMAbzz8Bd7lLN/lv0z0Kj6ZL343aK7cTLNgFcNdsi3mrfrIPeZvDxWKSLjSEqPScxYhxFeDyApgWcxJ8oe5DQTxOHNvI1JlCdY/KrbyV2XqyBR2kERuHb3TLFQ+tQ07ggtICbyisBKLMS0YAyuVISsz2pmatfluLtXE4TgY5xaimQh62b0FETvPQNuPvez+LQy78ZN9DWFKD1f3dERpj2g7LnmC2lMQKBgQDq+SFQR/zrkDpyUGifEMbns066ZjkPWoK6dGx/fvJKFxPv1WTT3ghkiq7PUvZCQ9v+4nLpR4YUaxkhLChUiFsbczd+lw4UY12jlO8JFLdQNkVfYsd4+DevQ8mVLCNTDnc1pe9CYdICUWsBBiCzHC4P3ThnG8MquF3g2TrB0/LPkwKBgQDbZpUtte6W97jwvgJvlKj2kpdHk0comF48lyOJaipvOdj3fkrQPH+oi4KCy1y9kMlgX89fTANQ3UqQ+3zhRZea+rOby29d+4+6Y1NL8qn6dFV/N5n/5vvkqsw2iswhEi8T0nXytqXJkhrd5FlcuagpIj1zo9lDmoyGfi+FcU4ZBwKBgQDn6x87auIy6vcVD1JD9b4swOjqx6x4AADZ4cGZEYY5JJAT/w52o0arXcpubVcMTogcCgwbPfITwyVZfYkM7kzmShEzDArkirLIm15XGzBXpklQfWGef3gOsByN9LOk7bkxpWcCoSQ0D4JKz26E+kJofBOIiXlXeAOIwz/gQb79qwKBgAhK10aUAsGH+WB3/gWTm9M99SPKaD0bjSxDFh/CeHoduJqaFl/KeZS4OSWSZB3yE/plhKP4boOm6YOTTcQB6ln8Mb+or7vgny0PRf2v3UbPENAvHM30P/8DiZZiJpb1Zfwcz0JuLtPwhr1uPQZpKfbsCWCOh86rT7ZhnJAxmR8PAoGAD4A3sukE+AuJz6/BgMjwZQZT0IaxTg+KcRLQyWmQ2xj+t8aNUwUIKLNcJR3Of2jSf0Gdval0rdnXfK1YXFX+473QhJKZAYWbFt1JzF9GNYa4K/J2wU4oDlAGIj0epV8k5JHg5Lj4FtgrqaTEJFNaZid219Xd8cX4afeauxMoWcY=";

    // 支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvcKjVG3uK7n2dDjDq3jP4XoW3HByLPLMXJGnkLNSmhzft/3OZw7Q7dgRvk/SahoudBn97myc1+utjuSBIW0/sPplo2OrP3CUml4LtU6o9VFfAzOunZ1bwyH1adRkWHO24qT5jNnwGJoByCRunk1rK/qqc1ML7CGQPl9E3lCRyVQWI8qhpY64YbuIzSLXwVf/HuucxmFnS9J5Z7gk3qdylDJz9p8tk4EUkolKb27yX5SCuNegcEMO1IHfongdyoMQSkpKZLdCiz46/Dn0/hQ8uvR3SU2X4gQhX0UEd7ZCcxuqKWVquplwvf7pSeygudQle5A8UX5mUljZbx1xWY4F0wIDAQAB";

    // 服务器异步通知页面路径
    public static final String NOTIFY_URL = "http://g8a65b28.natappfree.cc/alipay/notify";

    // 页面跳转同步通知页面路径
    public static final String RETURN_URL = "http://g8a65b28.natappfree.cc/alipay/return";

    // 签名方式
    public static final String SIGN_TYPE = "RSA2";

    // 字符编码格式
    public static final String CHARSET = "UTF-8";

    // 支付宝网关
    public static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
    // 实例化客户端
    public static AlipayClient alipayClient = new DefaultAlipayClient(
            GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
}