package com.wx.common.util;


import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 微信支付密文工具类测试
 */
public class WxPayCipherUtilTest {

    @Test
    public void testDecryptToString() {
        // 测试数据来自微信支付文档示例
        String apiV3Key = "7Uhb6ygv5tfc4rdx3esz2wa1q0pl9okm"; // 实际使用时替换为真实的API V3密钥
        String associatedData = "refund";
        String nonce = "xtAdUXBUaJyF";
        String ciphertext = "PYuNVEFbJO2K0LxRarjQoy4KYH683XBjCqa5dVG3XUtk7M4ONeRqlu3T0LsQvBzSogBkEjlhoXu7/otZofCHtk1TSPDLwOZ5K35K1iDJq9dFYgI+y1st5oKPQrQPtWQ9aPXgJUdTqp3KjE1xy6qsRBX/medFzXmAHgFgqL7S/dECTMIHRBrB5sEpNRSkJ6t3Q4tCZ47EcRedKxh2b+Lmx5U21nEOMvpdO/i0IT27ywuMYQnGeZCiZzIc542IUzYqcYLKBGJZ6oKcG2jl8I31lzUJoeL+6q+rHoIPcOShUXkTkyk4QuqC3D+QJ9HACLT57ZveH5kx5dnM2EmPmtUh1OUt25Ws8fy2TUuNhnDD9uyxjWh72z21YFawowfGHh0pm25CtiRUxrnw1y8bQyXwBaCvE0G+v4pH+OHW3Cew+qN4AgqP9+xeim7Ajkkkk9WAeIZwbqR8UaXnvgvc3Bx0F7RxYpCK7oLkmdb39igERRaaetueqP1pqXWII4f+lKRZ2xQmq9AdvzfiDPbaifImVeXynMDjCMNW11sjhZkbkSyqzh97sQ==";

        try {
            String decrypted = WxPayCipherUtil.decryptToString(apiV3Key, associatedData, nonce, ciphertext);
            assertNotNull(decrypted, "解密结果不应为null");

            // 验证解密后的数据包含预期的字段
            assertTrue("解密后的数据应包含退款单号", decrypted.contains("refund_id"));
            assertTrue("解密后的数据应包含商户退款单号", decrypted.contains("out_refund_no"));
            assertTrue("解密后的数据应包含微信支付订单号", decrypted.contains("transaction_id"));
            assertTrue("解密后的数据应包含商户订单号", decrypted.contains("out_trade_no"));

        } catch (Exception e) {
            fail("解密过程不应抛出异常: " + e.getMessage());
        }
    }


//    @Test
//    public void testDecryptWithEmptyParams() {
//        String apiV3Key = "your-api-v3-key";
//
//        // 测试空的关联数据
//        assertThrows(RuntimeException.class, () -> {
//            WxPayCipherUtil.decryptToString(apiV3Key, "", "nonce", "ciphertext");
//        }, "空的关联数据应该抛出异常");
//
//        // 测试空的随机串
//        assertThrows(RuntimeException.class, () -> {
//            WxPayCipherUtil.decryptToString(apiV3Key, "associatedData", "", "ciphertext");
//        }, "空的随机串应该抛出异常");
//
//        // 测试空的密文
//        assertThrows(RuntimeException.class, () -> {
//            WxPayCipherUtil.decryptToString(apiV3Key, "associatedData", "nonce", "");
//        }, "空的密文应该抛出异常");
//    }
}
