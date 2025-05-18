package com.wx.common.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetOrderDetailByTradeNo {

    private String tradeNo;
}
