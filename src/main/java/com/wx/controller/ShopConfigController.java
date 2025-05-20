package com.wx.controller;

import com.wx.common.model.Response;
import com.wx.common.model.ShopConfigResponse;
import com.wx.common.model.request.ShopConfigRequest;
import com.wx.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop/config")
@RequiredArgsConstructor
public class ShopConfigController {
    private final ShopService shopService;

    @RequestMapping(value = "/getShopConfigInfo", method = {RequestMethod.POST})
    public Response<?> getShopConfigInfo(@RequestBody ShopConfigRequest request) {
        try {
            ShopConfigResponse shopConfigInfo = shopService.getShopConfigInfo(request);
            return Response.success(shopConfigInfo);
        } catch (Exception e) {
            return Response.failure("getShopConfigInfo is error , " + e.getMessage());
        }
    }
}
