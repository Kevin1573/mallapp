package com.wx.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumUtil {

    /**
     * 随机将一个整数划分为count份
     *
     * @param num 待划分的整数
     * @return 划分后的数组
     */
    public static List<Integer> random(int num, int count, int max, int min) {
        Random random = new Random();
        List<Integer> result = new ArrayList<>(count);
        for (int i = 0; i <= count - 1; i++) {
            if (num == 0) {
                result.add(0);
                continue;
            }
            if (i == count - 1) {
                result.add(num);
                continue;
            }
            int part = random.nextInt(max - min + 1) + min;
            if (num <= part) {
                part = num;
            }
            result.add(part);
            num -= part;
        }
        return result;
    }

    // 判断小数点位数
    public static int countDecimalPlaces(double number) {
        String num = String.valueOf(number);
        int decimalPointIndex = num.indexOf('.');
        if (decimalPointIndex == -1) {
            return 0;
        }
        return num.length() - decimalPointIndex - 1;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            Random random = new Random();
            int income = random.nextInt(62 - 42 + 1) + 42;
            List<Integer> list = random(income, 30, 4, 0);
            int num = 0;
            for (Integer integer : list) {
                num += integer;
            }
            if (income != num) {
                System.out.print("error error, " + income + "  !=  " + num);
            }
        }
        System.out.print("end");
    }

}
