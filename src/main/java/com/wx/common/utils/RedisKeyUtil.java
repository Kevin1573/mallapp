package com.wx.common.utils;


import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {

    private RedisKeyUtil() {
    }

    // 单用户多少天内不可以播放相同的视频(单位：天)
    public static String buildPlayVideoLimitDaysKey() {
        return "PlayVideoLimitDays";
    }

    // 设备播放视频刷新时间（单位：小时）
    public static String buildPlayVideoUpdateKey() {
        return "PlayVideoUpdate";
    }

    // 缓存用户目前时间段内的推荐视频
    public static String buildCustomVideoKey(String device) {
        return "CustomVideo" + device;
    }

    // 设备收益缓存key
    public static String buildDeviceIncomeKey(String device) {
        return "Income" + device;
    }

    // 单个账号每15分钟中钻数最大值
    public static String buildDiamondMax() {
        return "DiamondMax";
    }

    // 单个账号每15分钟中钻数最小值
    public static String buildDiamondMin() {
        return "DiamondMin";
    }

    // 总抽数最大值
    public static String buildLotteryAllMax() {
        return "LotteryAllMax";
    }

    // 总抽数最小值
    public static String buildLotteryAllMin() {
        return "LotteryAllMin";
    }

    // 抽中数最大值
    public static String buildLotteryWinMax() {
        return "LotteryWinMax";
    }

    // 抽中数最小值
    public static String buildLotteryWinMin() {
        return "LotteryWinMin";
    }

    // 单个设备每15分钟收益范围
    public static String buildDeviceIncomeScope(String mac) {
        return "DeviceIncomeScope" + mac;
    }

    // 单个设备每15分钟收益范围默认值
    public static String buildDeviceIncomeScopeDefault() {
        return "DeviceIncomeScopeDefault";
    }

    // 抽奖奖品绑定用户
    public static String buildPrizeBindUser(String prize, String level) {
        return "PrizeBind" + level + prize;
    }

    // 用户抽奖获得的幸运值
    public static String buildUserLuckNum(String user) {
        return "UserLuckNum" + user;
    }

}
