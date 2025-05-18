package com.wx.common.model.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderGoodsRequest {

    private String token;

    private Long addrId;

    /**
     * 优惠券
     */
    private Long couponId;

    /**
     * 提货方式；1邮寄，2自提
     */
    private Integer logisticsType;

    /**
     * 运费
     */
    private Double freight;

    private List<OrderGoodsModelRequest> modelRequestList;
}
