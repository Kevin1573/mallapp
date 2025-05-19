package com.wx.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnResponse {
    // 订单编号
    private String tradeNo;
    // 支付方式
    private String payWay;
    // 订单状态
    private String status;
    // 实付金额
    private BigDecimal payAmount;
    // 下单时间
    private Date orderTime;
}
