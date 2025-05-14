package com.wx.common.model.response;

import lombok.Data;

import java.util.List;

@Data
public class QueryUserPointHisResponse {

    private Long page;

    private Long limit;

    private Long total;

    private List<QueryUserPointHisModel> userPointHisModelslist;
}
