package com.wx.common.model.response;

import lombok.Data;

@Data
public class CommitOrderResponse {

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 物流价格
     */
    private Double logisticsPrice;

    /**
     * 订单总价
     */
    private Double totalPrice;

    /**
     * 积分
     */
    private Long point;

    /**
     * 其他积分
     */
    private Long realPoint;
}
