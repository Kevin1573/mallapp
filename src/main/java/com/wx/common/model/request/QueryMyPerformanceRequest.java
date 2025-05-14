package com.wx.common.model.request;

import lombok.Data;

@Data
public class QueryMyPerformanceRequest {

    private String token;

    private long startTimeStamp;

    private long endTimeStamp;

    /**
     * 充值积分类型；1积分 2其他积分
     */
    private Integer pointType;

    /**
     * 业绩类型；1直推 2间推
     */
    private Integer type;

    private Long page;

    private Long limit;
}
