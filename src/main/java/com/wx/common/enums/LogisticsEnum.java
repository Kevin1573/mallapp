package com.wx.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
@AllArgsConstructor
public enum LogisticsEnum {

    /**
     * 一区
     */
    JIANGSU("江苏", 4.0, 0.8),
    ZHEJIANG("浙江", 4.0, 0.8),
    SHANGHAI("上海", 4.0, 0.8),
    ANHUI("安徽", 4.0, 0.8),

    /**
     * 二区
     */
    JIANGXI("江西", 3.8, 2.4),
    FUJIAN("福建", 3.8, 2.4),
    BEIJING("北京市", 3.8, 2.4),
    TIANJIN("天津市", 3.8, 2.4),
    HEBEI("河北", 3.8, 2.4),
    HUNAN("湖南", 3.8, 2.4),
    SHANDONG("山东", 3.8, 2.4),
    GUANGDONG("广东", 3.8, 2.4),
    HUBEI("湖北", 3.8, 2.4),
    HENAN("河南", 3.8, 2.4),

    /**
     * 三区
     */
    SHANIXI("陕西", 3.8, 3.5),
    SHAN_XI("山西", 3.8, 3.5),
    SICHUAN("四川", 3.8, 3.5),
    CHONGQING("重庆", 3.8, 3.5),
    GUANGXI("广西", 3.8, 3.5),
    GUIZHOU("贵州", 3.8, 3.5),
    JILIN("吉林省", 3.8, 3.5),
    LIAONING("辽宁", 3.8, 3.5),
    YUNNAN("云南", 3.8, 3.5),
    HEILONGJIANG("黑龙江", 3.8, 3.5),

    /**
     * 四区
     */
    NEIMENGGU("内蒙古", 5.7, 5.7),
    QINGHAI("青海", 5.7, 5.7),
    GANSU("甘肃", 5.7, 5.7),
    NINGXIA("宁夏", 5.7, 5.7),
    HAINAN("海南", 5.7, 5.7),

    /**
     * 五区
     */
    XINJIANG("新疆", 15.0, 15.0),
    XIZANG("西藏", 15.0, 15.0),

    ;

    public static LogisticsEnum getEnumByCode(String addr) {
        if (StringUtils.isEmpty(addr)) {
            return null;
        }
        for (LogisticsEnum logisticsEnum : LogisticsEnum.values()) {
            if (addr.contains(logisticsEnum.province)) {
                return logisticsEnum;
            }
        }
        return null;
    }

    // 省份
    private String province;

    // 首重
    private Double first;

    // 后续
    private Double next;
}
