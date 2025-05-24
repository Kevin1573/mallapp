package com.wx.service;

import com.wx.common.model.ShopConfigResponse;
import com.wx.common.model.request.ShopConfigRequest;
import com.wx.common.model.response.ShopConfigDOResponse;

public interface ShopService {
    ShopConfigResponse getShopConfigInfo(ShopConfigRequest request);

    ShopConfigDOResponse getShopInfo(ShopConfigRequest request);
}
