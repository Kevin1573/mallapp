package com.wx.common.model.response;

import lombok.Data;

import java.util.List;

@Data
public class QueryMyPerformanceResponse {

    /**
     * 一共使用积分
     */
    private Long totalUsedPoint;

    /**
     * 一共使用其他积分
     */
    private Long totalUsedRealPoint;

    /**
     * 一共充值积分
     */
    private Long totalRechargePoint;

    /**
     * 一共充值其他积分
     */
    private Long totalRechargeRealPoint;

    private Long page;

    private Long limit;

    private Long total;


    /**
     * 充值流水记录
     */
    private List<QueryMyPerformanceModel> modelList;
}
