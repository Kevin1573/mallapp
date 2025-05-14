package com.wx.common.model.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderGoodsRequest {

    private String token;

    private Long addrId;

    /**
     * 提货方式；1邮寄，2自提
     */
    private Integer logisticsType;

    private List<OrderGoodsModelRequest> modelRequestList;
}
