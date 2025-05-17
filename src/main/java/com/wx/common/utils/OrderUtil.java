package com.wx.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;

public class OrderUtil {
    // 方案1：时间戳+随机数（适合低并发）
    public static String simpleOrderNo() {
        return DateUtil.format(DateUtil.date(), "yyyyMMddHHmmssSSS")
                + RandomUtil.randomNumbers(4);
    }

    // 方案2：雪花算法（推荐分布式场景）
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    public static String snowflakeOrderNo() {
        return "ORDER_" + SNOWFLAKE.nextIdStr();
    }

    // 方案3：UUID简化版（保证唯一性）
    public static String uuidOrderNo() {
        return "O" + IdUtil.simpleUUID().substring(0, 15).toUpperCase();
    }
}
