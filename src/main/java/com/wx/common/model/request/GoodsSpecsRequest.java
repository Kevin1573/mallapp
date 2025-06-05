package com.wx.common.model.request;

import lombok.Data;

@Data
public class GoodsSpecsRequest {
    private int num;
    private String name;
    private String specs; // 规格
}
