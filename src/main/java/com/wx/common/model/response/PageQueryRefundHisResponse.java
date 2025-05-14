package com.wx.common.model.response;

import lombok.Data;

import java.util.List;

@Data
public class PageQueryRefundHisResponse {

    private List<QueryRefundHisModel> data;

    private Long limit;

    private Long page;

    private Long total;
}
