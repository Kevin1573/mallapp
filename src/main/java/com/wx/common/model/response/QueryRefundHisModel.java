package com.wx.common.model.response;

import lombok.Data;

import java.util.Date;

@Data
public class QueryRefundHisModel {

    private String status;

    private String phone;

    private String tradeNo;

    /**
     * 订单详情
     */
    private String desc;

    private Date createTime;

    private Double money;
}
