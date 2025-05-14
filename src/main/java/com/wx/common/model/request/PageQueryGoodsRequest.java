package com.wx.common.model.request;

import lombok.Data;

@Data
public class PageQueryGoodsRequest {

    private Long page;

    private Long limit;

    private String typeId;
}
