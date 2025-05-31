package com.wx.common.model;

import com.wx.common.model.request.QueryOrderGoodsModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderListItem {
    private String tradeNo;

    private BigDecimal totalPrice;
    private Integer isPaySuccess;
    private String orderInfo;
    private List<QueryOrderGoodsModel> goodsList;
    private String logisticsId;
    private Integer status;
    private Integer isComplete;
    private String logistics;
    private Integer payWay;
    private Double payAmount;
    private Date createTime;
}