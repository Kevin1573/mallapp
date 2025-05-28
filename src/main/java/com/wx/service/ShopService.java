package com.wx.service;

import com.wx.common.model.ShopConfigResponse;
import com.wx.common.model.request.BestSellingGoodsRequest;
import com.wx.common.model.request.ShopConfigRequest;
import com.wx.common.model.request.ShopRebateRequest;
import com.wx.common.model.response.ShopConfigDOResponse;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.entity.RebateDO;

import java.util.List;

public interface ShopService {
    ShopConfigResponse getShopConfigInfo(ShopConfigRequest request);

    ShopConfigDOResponse getShopInfo(ShopConfigRequest request);

    Boolean updateBestSellingGoods(BestSellingGoodsRequest request);

    void initShopUserLevelDiscount(String fromMall);

    List<RebateDO> getRebateList(ShopConfigRequest request);

    Boolean editRebateList(ShopRebateRequest request);

    List<GoodsDO> selectListByFromMall(String fromMall);
}
