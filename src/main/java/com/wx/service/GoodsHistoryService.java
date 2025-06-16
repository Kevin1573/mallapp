package com.wx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wx.common.model.request.OrderListRequest;
import com.wx.orm.entity.GoodsHistoryDO;
import com.wx.orm.entity.UserProfileDO;

import java.math.BigDecimal;

public interface GoodsHistoryService  extends IService<GoodsHistoryDO> {
    BigDecimal totalAmountGoodsByTime(OrderListRequest request, UserProfileDO userProfile);
}
