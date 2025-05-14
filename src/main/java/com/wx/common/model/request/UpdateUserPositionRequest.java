package com.wx.common.model.request;

import lombok.Data;

@Data
public class UpdateUserPositionRequest {

    private Long id;

    private String position;

    private Long point;

    private Long realPoint;
}
