package com.wx.common.utils;


import com.wx.common.exception.BizException;
import org.apache.commons.lang.time.DateFormatUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    private static final String FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";

    /**
     * 获取某个时间，n 年/月/周/日/时/分/秒前或者后的时间
     * n为正时代表时间戳后的某个时间，n为负时代表时间戳前的某个时间，
     *
     * @param date     当前日期
     * @param time     前n天则为负数，后n天则为正数
     * @param timeType 时间类型（时分秒）
     * @return 时间戳
     */
    public static Date getDateAfterTime(Date date, int time, int timeType) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(timeType, time);
            return cal.getTime();
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
    }

    /**
     * 获取当前时间到明天凌晨两点的时间间隔（单位：秒）
     *
     * @param currentTime 当前时间
     * @return 时间间隔秒数
     */
    public static int getTimeDiff(Date currentTime) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(currentTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime tomorrowTime = LocalDateTime.ofInstant(currentTime.toInstant(), ZoneId.systemDefault())
                .plusDays(1).withHour(2).withMinute(0).withSecond(0).withNano(0);
        return (int) ChronoUnit.SECONDS.between(localDateTime, tomorrowTime);
    }

    /**
     * 获取当前时间到明天凌晨0点的时间间隔（单位：秒）
     *
     * @param currentTime 当前时间
     * @return 时间间隔秒数
     */
    public static int getTimeToEnd(Date currentTime) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(currentTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime tomorrowTime = LocalDateTime.ofInstant(currentTime.toInstant(), ZoneId.systemDefault())
                .plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return (int) ChronoUnit.SECONDS.between(localDateTime, tomorrowTime);
    }

    /**
     * 时间格式化
     *
     * @param date 入参
     * @return 返回固定时间格式
     */
    public static String dateFormat(Date date) {
        return DateFormatUtils.format(date, FORMAT_ISO8601);
    }

    /**
     * 获取当天零点时间
     *
     * @return date
     */
    public static Date getDayBeginTime() {
        // 创建Calendar实例
        Calendar calendar = Calendar.getInstance();

        // 将当前日期时间的时、分、秒、毫秒部分设置为0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 获取修改后的日期时间
        return calendar.getTime();
    }

    /**
     * 获取当天末尾时间
     *
     * @return date
     */
    public static Date getDayEndTime() {
        // 创建Calendar实例
        Calendar calendar = Calendar.getInstance();

        // 将当前日期时间的时、分、秒、毫秒部分设置为0
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        // 获取修改后的日期时间
        return calendar.getTime();
    }

    /**
     * 获取本周开始时间
     *
     * @return date
     */
    public static Date getWeekBeginTime() {
        Calendar cal = Calendar.getInstance();
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取本周结尾时间
     *
     * @return date
     */
    public static Date getWeekEndTime() {
        Calendar cal = Calendar.getInstance();
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DAY_OF_WEEK, 7 - day + 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 获取本月第一天0点时间
     */
    public static Date getMonthBeginTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * 获得本月最后一天24点时间
     */
    public static Date getMonthEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }

    /**
     * 判断给定时间距今是否超过一年
     *
     * @param date
     */
    public static boolean getDiffIsThanOneYear(Date date) {
        Date currentDate = new Date();
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);
        Calendar givenCalendar = Calendar.getInstance();
        givenCalendar.setTime(date);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int givenYear = givenCalendar.get(Calendar.YEAR);
        return (currentYear - givenYear) >= 1;
    }


    public static void main(String[] args) {
        System.out.print(getDateAfterTime(new Date(), -3, Calendar.DATE));

    }
}

