package com.wx.common.model.response;

import lombok.Data;

@Data
public class QueryMyPerformanceModel {

    private String nickName;

    private String headUrl;

    /**
     * 上级邀请人昵称
     */
    private String inviteUserName;

    /**
     * 上级邀请人头像
     */
    private String inviteUserHeadUrl;

    /**
     * 已使用积分
     */
    private Long usedPoint;

    /**
     * 已使用其他积分
     */
    private Long usedRealPoint;

    /**
     * 已充值积分
     */
    private Long rechargePoint;

    /**
     * 已充值其他积分
     */
    private Long rechargeRealPoint;

}
