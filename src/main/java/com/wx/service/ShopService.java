package com.wx.service;

import com.alibaba.fastjson.JSONObject;
import com.wx.common.model.ShopConfigResponse;
import com.wx.common.model.request.*;
import com.wx.common.model.response.ShopConfigDOResponse;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.entity.RebateDO;

import java.math.BigDecimal;
import java.util.List;

public interface ShopService {
    ShopConfigResponse getShopConfigInfo(ShopConfigRequest request);

    ShopConfigDOResponse getShopInfo(ShopConfigRequest request);

    Boolean updateBestSellingGoods(BestSellingGoodsRequest request);

    void initShopUserLevelDiscount(String fromMall);

    List<RebateDO> getRebateList(ShopConfigRequest request);

    Boolean editRebateList(ShopRebateRequest request);

    List<GoodsDO> selectListByFromMall(String fromMall);

    Boolean updateRecommendedGoods(RecommendedGoodsRequest request);

    JSONObject selectRecommendListByFromMall(String fromMall);

    void initShopAccount(String contactPhone, String fromMall);

    BigDecimal getFreight(String fromShopName);

    Boolean prepareRefund(OrderRequest request);
}
