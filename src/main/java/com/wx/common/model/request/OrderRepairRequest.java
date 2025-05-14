package com.wx.common.model.request;

import lombok.Data;

@Data
public class OrderRepairRequest {

    private String token;

    /**
     * 报修情况描述
     */
    private String description;
}
