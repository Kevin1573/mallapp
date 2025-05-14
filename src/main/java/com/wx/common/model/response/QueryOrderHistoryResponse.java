package com.wx.common.model.response;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class QueryOrderHistoryResponse {

   private List<QueryOrderHistoryModel> data;

   private Long page;

   private Long total;

   private Long limit;
}

