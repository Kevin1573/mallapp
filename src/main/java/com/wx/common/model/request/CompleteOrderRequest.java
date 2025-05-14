package com.wx.common.model.request;

import lombok.Data;

@Data
public class CompleteOrderRequest {

    private String tradeNo;

    /**
     * 物流信息
     */
    private String logistics;

    /**
     * 物流订单号
     */
    private String logisticsId;
}
