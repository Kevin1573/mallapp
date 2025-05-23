package com.wx.admin.controller;

import com.wx.common.exception.BizException;
import com.wx.common.model.ApiResponse;
import com.wx.common.model.request.ShopConfigRequest;
import com.wx.common.model.response.ShopConfigDOResponse;
import com.wx.service.ShopService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @RequestMapping(value = "/getInfo", method = {RequestMethod.POST})
    public ApiResponse<ShopConfigDOResponse> getShopInfo(@RequestBody ShopConfigRequest request,
                                                         @RequestHeader("Authorization") String authHeader) {
        try {
            if (StringUtils.isNotBlank(authHeader) && StringUtils.isBlank(request.getToken())) {
                request.setToken(authHeader);
            }
            if (StringUtils.isBlank(request.getToken())) {
                throw new BizException("用户没有登录, 或者token 失效");
            }
            ShopConfigDOResponse shopConfigInfo = shopService.getShopInfo(request);
            return ApiResponse.success(shopConfigInfo);
        } catch (Exception e) {
            return ApiResponse.fail(400, "getShopConfigInfo is error , " + e.getMessage());
        }
    }

}
