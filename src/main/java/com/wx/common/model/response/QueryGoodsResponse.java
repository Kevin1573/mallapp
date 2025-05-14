package com.wx.common.model.response;

import lombok.Data;

import java.util.List;

@Data
public class QueryGoodsResponse {

    private List<QueryGoodsModel> records;

    /**
     * 一共有多少条数据
     */
    private Long total;

    /**
     * 当前页有多少条数据
     */
    private Long limit;

    /**
     * 页数
     */
    private Long page;
}
