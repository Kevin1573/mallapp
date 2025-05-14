package com.wx.common.model.request;

import lombok.Data;

@Data
public class UserPointNewRequest {


    private Long userId;

    /**
     * 增减的用户积分，增加为正数，减少为负数
     */
    private Integer point;

    /**
     * 增减的用户其他积分，增加为正数，减少为负数
     */
    private Integer realPoint;
}
