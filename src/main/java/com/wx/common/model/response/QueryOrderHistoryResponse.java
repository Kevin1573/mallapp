package com.wx.common.model.response;

import lombok.Data;

import java.util.List;

@Data
public class QueryOrderHistoryResponse {

   private List<QueryOrderHistoryModel> records;

   private Double totalAmount;

   private Long page;

   private Long total;

   private Long limit;
}

