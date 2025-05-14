package com.wx.common.model.response;

import lombok.Data;

import java.util.Date;

@Data
public class QueryUserPointHisModel {
    /**
     * 积分
     */
    private Integer point;

    /**
     * 充值积分
     */
    private Integer realPoint;

    /**
     * 充值时间
     */
    private Date createTime;

}
