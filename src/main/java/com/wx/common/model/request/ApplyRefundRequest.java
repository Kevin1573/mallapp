package com.wx.common.model.request;

import lombok.Data;

@Data
public class ApplyRefundRequest {

    /**
     * 对应historyDO的主键id
     */
    private Long orderId;

    private String phone;
}
