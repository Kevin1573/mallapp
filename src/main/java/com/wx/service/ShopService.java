package com.wx.service;

import com.wx.common.model.ShopConfigResponse;
import com.wx.common.model.request.ShopConfigRequest;

public interface ShopService {
    ShopConfigResponse getShopConfigInfo(ShopConfigRequest request);
}
