package com.wx.common.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;

public class AlipayConfigV3 {
    // 应用ID
    public static final String APP_ID = "2021005142672288";

    // 应用私钥
    public static final String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCFK+8jMfN6SsztH7odpynXVJLH3bCAQWmAX2ZAX5GcwrshyVxE33yxmNDa9RuRKvijt8TPxKahz8/mpYAVgz27Feg+7NDkNKJHhxPCrdEmYTpi/P4lBBy6VCG2Uon4JyksBgdoeTTom5ptu/KPL+S75rw4SgT+Bi+vgSkn+eX0HavLY9ElemtJl2Je0TNiCUPVXBPWGye3+d12c5/fo4Tfa5RiCbkf5D8brg/PdISfivp7yKBRcQfG2v/XE9g0CSFfk+0Max1hyKAvMCuuuFG/eck+z7WXCn2V+tM9ezeLQSkJ8Tk6venPhJ7En/XINFF78wdYsSj516ju8C9SkYDFAgMBAAECggEAGcOJLU7xotwrz0hPEoOPpbgX+ZDbH8UH6y0JtDSJEEHgQEYbkruF/TnqViILZkdg+ROJgQSzlCSHPUERiZftbm9rkhjwxOWl656nZQeJqhovWI1HgdPM1wOFzicjyB2Tao/0CPNCGCpacxZkc+05HF0kVuODGb4KpbKgSrqfEV4PiNLxUNSpLUHStVe8Ls6Ft/dxNOhI1HYng+WKa+3pISloOxbawg46FUcPMTDRr2P1VLBB+LWTaJ8dzKn7tpnl4hQmrhmlRFgXXMCGYvWCvOHRL75RYR5WH4q3NF/81iuR16YS+6NytOxg4XcDxIEyBTaTkLFq7Y5bBQoVH68R4QKBgQDddZb9V0isQik7lKHUhcxXVloe4aA7YqdEpeDLfZMfYp0VRlrRf88voN4Q8KkG4z2HDoNUXidm+Ymm+uktw6kLSKDlBxDbdjv9rRwwNCtfb1NzoKKxftzoe64SFFOhyj0a3zIK+VksoaggDUXJTSVfQOw5kh3sKpoNsyTvx9pWmQKBgQCZ8TMXXmC1w1i/+Xu4x4xdQJMBKdWvubZCb+sy2L0/z1YHpiFZhhjQK9Kr5qYe0GZPvIxgGfuB3ZnnktFQRmLc468dN1vYXMrt9uzE1RvSvzA01jcrZbJP85O12h+qd3PKlW/g3qGbg3LcPlSOVdjsz6jhYCWwhh+xLE4z79PTDQKBgQDJ/tSu1WtK+7niR2rojvXYf8IJwuDE6fIJ0odc9Hg6O1duLVLvHfXbyo5iwzBZQPJA+wLc9FhaO6fLr7b6cCoDSca/wBcnJgwIEVrRuPQKw19J6y4aWc2jHBSlFvdFblMX7JcXnWIyLG1Oduy9dOlunagZxuYWILGNZ2Z8czz6mQKBgFVXbieKXrrlbRIgUQKDPcGExvpiIuCkZ/I08YLivSfe3aan5t0mhiMB6Gz8FSQIDHAv6vJr9Q5tt6C1t+x02OH0J2RfCD/OUYHS3jfQnPgcnbDtAPDCFDRWqJCsN9ndFwg8CCX1xknceZS3yVCPLw85oDoxfzN5Z6BlG4WXcysRAoGBALE0MUQXBCMXz5a6WybYR21dMd4mA+iwtoz0alRAvTMjx2OgMuyP5qiDRceUKVvqUcIYPDs/GChwZ/oHFN7QrdrPeUiq0cgvVHgs//ZODnuyUC9cKWkx3CqSN3NvIChyOwdCrWwwLBUgv0Ry/k4L7aGgNkJd4JlTF6qKeudaII5a";

    // 支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvcKjVG3uK7n2dDjDq3jP4XoW3HByLPLMXJGnkLNSmhzft/3OZw7Q7dgRvk/SahoudBn97myc1+utjuSBIW0/sPplo2OrP3CUml4LtU6o9VFfAzOunZ1bwyH1adRkWHO24qT5jNnwGJoByCRunk1rK/qqc1ML7CGQPl9E3lCRyVQWI8qhpY64YbuIzSLXwVf/HuucxmFnS9J5Z7gk3qdylDJz9p8tk4EUkolKb27yX5SCuNegcEMO1IHfongdyoMQSkpKZLdCiz46/Dn0/hQ8uvR3SU2X4gQhX0UEd7ZCcxuqKWVquplwvf7pSeygudQle5A8UX5mUljZbx1xWY4F0wIDAQAB";
    // 服务器异步通知页面路径
    public static final String NOTIFY_URL = "http://g8a65b28.natappfree.cc/alipay/notify";

    public static final String CALLBACK_URL = "http://g8a65b28.natappfree.cc/alipay/callback";

    // 页面跳转同步通知页面路径
    public static final String RETURN_URL = "http://g8a65b28.natappfree.cc/alipay/payReturn";

    // 签名方式
    public static final String SIGN_TYPE = "RSA2";

    // 字符编码格式
    public static final String CHARSET = "UTF-8";
    public static final String FORMAT = "json";

    // 支付宝网关
//    public static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
    public static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    public static final String SERVER_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    // 实例化客户端
    public static AlipayClient alipayClient = new DefaultAlipayClient(
            GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);


    // 正式环境的配置
    public static AlipayConfig getAlipayConfig() {
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCFK+8jMfN6SsztH7odpynXVJLH3bCAQWmAX2ZAX5GcwrshyVxE33yxmNDa9RuRKvijt8TPxKahz8/mpYAVgz27Feg+7NDkNKJHhxPCrdEmYTpi/P4lBBy6VCG2Uon4JyksBgdoeTTom5ptu/KPL+S75rw4SgT+Bi+vgSkn+eX0HavLY9ElemtJl2Je0TNiCUPVXBPWGye3+d12c5/fo4Tfa5RiCbkf5D8brg/PdISfivp7yKBRcQfG2v/XE9g0CSFfk+0Max1hyKAvMCuuuFG/eck+z7WXCn2V+tM9ezeLQSkJ8Tk6venPhJ7En/XINFF78wdYsSj516ju8C9SkYDFAgMBAAECggEAGcOJLU7xotwrz0hPEoOPpbgX+ZDbH8UH6y0JtDSJEEHgQEYbkruF/TnqViILZkdg+ROJgQSzlCSHPUERiZftbm9rkhjwxOWl656nZQeJqhovWI1HgdPM1wOFzicjyB2Tao/0CPNCGCpacxZkc+05HF0kVuODGb4KpbKgSrqfEV4PiNLxUNSpLUHStVe8Ls6Ft/dxNOhI1HYng+WKa+3pISloOxbawg46FUcPMTDRr2P1VLBB+LWTaJ8dzKn7tpnl4hQmrhmlRFgXXMCGYvWCvOHRL75RYR5WH4q3NF/81iuR16YS+6NytOxg4XcDxIEyBTaTkLFq7Y5bBQoVH68R4QKBgQDddZb9V0isQik7lKHUhcxXVloe4aA7YqdEpeDLfZMfYp0VRlrRf88voN4Q8KkG4z2HDoNUXidm+Ymm+uktw6kLSKDlBxDbdjv9rRwwNCtfb1NzoKKxftzoe64SFFOhyj0a3zIK+VksoaggDUXJTSVfQOw5kh3sKpoNsyTvx9pWmQKBgQCZ8TMXXmC1w1i/+Xu4x4xdQJMBKdWvubZCb+sy2L0/z1YHpiFZhhjQK9Kr5qYe0GZPvIxgGfuB3ZnnktFQRmLc468dN1vYXMrt9uzE1RvSvzA01jcrZbJP85O12h+qd3PKlW/g3qGbg3LcPlSOVdjsz6jhYCWwhh+xLE4z79PTDQKBgQDJ/tSu1WtK+7niR2rojvXYf8IJwuDE6fIJ0odc9Hg6O1duLVLvHfXbyo5iwzBZQPJA+wLc9FhaO6fLr7b6cCoDSca/wBcnJgwIEVrRuPQKw19J6y4aWc2jHBSlFvdFblMX7JcXnWIyLG1Oduy9dOlunagZxuYWILGNZ2Z8czz6mQKBgFVXbieKXrrlbRIgUQKDPcGExvpiIuCkZ/I08YLivSfe3aan5t0mhiMB6Gz8FSQIDHAv6vJr9Q5tt6C1t+x02OH0J2RfCD/OUYHS3jfQnPgcnbDtAPDCFDRWqJCsN9ndFwg8CCX1xknceZS3yVCPLw85oDoxfzN5Z6BlG4WXcysRAoGBALE0MUQXBCMXz5a6WybYR21dMd4mA+iwtoz0alRAvTMjx2OgMuyP5qiDRceUKVvqUcIYPDs/GChwZ/oHFN7QrdrPeUiq0cgvVHgs//ZODnuyUC9cKWkx3CqSN3NvIChyOwdCrWwwLBUgv0Ry/k4L7aGgNkJd4JlTF6qKeudaII5a"; //"<-- 请填写您的应用私钥，例如：MIIEvQIBADANB ... ... -->";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvcKjVG3uK7n2dDjDq3jP4XoW3HByLPLMXJGnkLNSmhzft/3OZw7Q7dgRvk/SahoudBn97myc1+utjuSBIW0/sPplo2OrP3CUml4LtU6o9VFfAzOunZ1bwyH1adRkWHO24qT5jNnwGJoByCRunk1rK/qqc1ML7CGQPl9E3lCRyVQWI8qhpY64YbuIzSLXwVf/HuucxmFnS9J5Z7gk3qdylDJz9p8tk4EUkolKb27yX5SCuNegcEMO1IHfongdyoMQSkpKZLdCiz46/Dn0/hQ8uvR3SU2X4gQhX0UEd7ZCcxuqKWVquplwvf7pSeygudQle5A8UX5mUljZbx1xWY4F0wIDAQAB"; //"<-- 请填写您的支付宝公钥，例如：MIIBIjANBg... -->";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do"); // "https://openapi.alipay.com/gateway.do"
        alipayConfig.setAppId("2021005142672288"); //"<-- 请填写您的AppId，例如：2019091767145019 -->"
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }

    public static AlipayConfig getAlipayConfigSX() {
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCE3tJKl+WoWKnZilx43FzcU7yTJALcAaAQtpjyJVjdpqMJc+mgoaHH/NCUr5VxDMP70LTBRo0auDCwsfVd7Vdf5UMZguN7nLr4UNm6e0Pv8HwTwERtWU4vy/qizOOs7eEgQkX4xyTIDjB4Lc+ShI+56G0YHnaQ58bQeVjAdqIPnAOMrNKjarlznW/x97KNr2dbDLvi0sz/Kr/QE4zFwdVhByFWQGqgl8SMZ8cBdnWXbLKKgTDYzIUNsSNAqJM+LrLQfSedeJWNsU3L9jIO4wEabaLQq2vda2/FZcPgd4qaB2cs4cr0YW/rsV6mYtywDM0fObggO9iEHl539Nis4tVDAgMBAAECggEAYaulq72SzTg3yrjBlvptrGxFb2gGThFHx2FidoVrmKt4p2c0cqa91NOLw/B6CvsWSdI2POkOqniBGjIGqv/fvZc6xtMHQ4OHkSdllYYs/4CoxKAN7M+8ALiRygCQ0DqgxWTSKLjC2ehMSttp/UtCl6c6IoOqJoSqCe9ZhUjvgkbWd0uFtKRWIVjDd/gNPiHGWalNUbDsUem4d0ZPpTbymjiimmx/yRlMFWCFuV1rLDycugdgN9N9owegzMrMw1qJ7KoZpmkTunLnIFDmK3bjj4wUtjsVtB4V1NcPgyVsK2rw0LzmWE8oIXJfqtjnqii23iuS84wuJdX/86Pt+RLQyQKBgQDsQ/jq4HYbcl83lwIjlXuj9HWNUBNLCkKOq+mszlu6HQ5Rdm43vIramjFQxUk3soWBlPySflxkWEsN/r2IBm8xwZ2yMw53yLcIgh7EPxYTJWcybbQ5Qw7idvy6vKUwt694bHvuQbJ+Wu+IfTikNYDzZGGp5z2oaScRP1lDSwQKVQKBgQCP9/fDu2Yb0IUZRSMoDDxZG83BoxkMPMXUcyw2o7zwBQWAr3PwTGUZGUBNgm0v5iXPptgluwrBl1zkCu0U2IlHbVglyz3bB1RlzauLL3OTPX5XgUWvaS6UyI67JrJm42Ui9oGS7w13SxlfcMHBjqhZYIlDzB1QOmcSP6uueukpNwKBgBAi5FFGW0qsmQw26BJ+Fx+vhofMPEUXwkv0OdVvkpw3d87gg86StfkQJuYZU1LWgn/DKSzoLG8+CIJAL7mmltmTG64cBpTvxHB0Up2coWlVWKL9Ex4GEnItJj2qvK2DHhzfa+dt3T77/vgXdVLhxJja9kCgZnmmFOD8rqAF85SxAoGAALdVDi4yddT1tIh5gkt9SVwwC9SJ3Vu4CDSCdcset3ru6hL1Wq6mmhdcEIajkGdurLfBv35MsWXVWnULdN0W4boThCycpChHLTJ2tfyIcKUPqdPEyviU+Lx+qUOqA4UCDch4IIj74ioHeEgRTqss/zTRufqb8rg3pwmWCwolesUCgYEAx1i+K1C2bg4p8cudy50hKA3lKExZV3esiQWTGO0+jJuFZaynbDMorvcXqntnPx6TIqYrMWZ1OFNGo3tRG03/7bLKz58762J5afKgeghypWouvGkpKrbH6vKwuQfFdVI0jolQyXgLDlQ4U/2ukw7H/VO7xxf6pq9TFQMCiHnV9wQ="; //"<-- 请填写您的应用私钥，例如：MIIEvQIBADANB ... ... -->";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAko47xPKaS0mcpHNJTKBsZUA02BRpRLMNctmdqbjjMNoChzUYgEM0qcH1fBsz2GUPNRycwMbNsNIP+DXC8k7T86epc7tA1ezkmYWKM93B49M3C2cWYXg+xBB+9yJCyeiTHMan6MPdGtERQgpu97nAtCDmqLR6plHN7rr+D3WRup1LzmrAiAx2ywbzpRabFe9eCwECd2kXKzxPvkTXhO2JItN0hVnpXrOmtuyVxlBrmjVEZRvZlPzPHOvTYBRHOdxEyANvno1lKI4Z8ZlbZ5LO8lPSY5MZqvEipLV0OdWsXdDuKMJsN6+ne8TvMlVh0VpHl0rTpDpOTJIk5nX+9RUn6QIDAQAB"; //"<-- 请填写您的支付宝公钥，例如：MIIBIjANBg... -->";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do"); // "https://openapi.alipay.com/gateway.do"
        alipayConfig.setAppId("2021000148682383"); //"<-- 请填写您的AppId，例如：2019091767145019 -->"
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }

    public static AlipayConfig getAlipayConfig2() {
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
}