package com.wx.common.utils;

import com.wx.common.enums.LogisticsEnum;

import java.util.Objects;

public class LogisticsUtil {

    /**
     * 根据快递重量和省份计算物流费
     * @param weight
     * @param addr
     * @return
     */
    public static Double getLogisticsPrice(Integer weight, String addr) {
        LogisticsEnum logisticsEnum = LogisticsEnum.getEnumByCode(addr);
        if (Objects.isNull(logisticsEnum)) {
            return null;
        }

        // 根据省份计算物流费
        Double logisticsPrice;
        if (weight <= 1) {
            logisticsPrice = logisticsEnum.getFirst();
        } else {
            Double nextPrice = (weight - 1) * logisticsEnum.getNext();
            logisticsPrice = nextPrice + logisticsEnum.getFirst();
        }

//        // 根据物流费加上杂物费
//        if (logisticsPrice <= 15) {
//            logisticsPrice += 4;
//        } else if (logisticsPrice <= 30) {
//            logisticsPrice += 5;
//        } else if (logisticsPrice <= 50) {
//            logisticsPrice += 6;
//        } else {
//            logisticsPrice += 9;
//        }

        return logisticsPrice;
    }

    public static void main(String[] args) {
        Double logisticsPrice = getLogisticsPrice(6, "安徽省");
        System.out.print(logisticsPrice);
    }
}
