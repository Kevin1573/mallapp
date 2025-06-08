package com.wx.controller;

import com.wx.common.model.ApiResponse;
import com.wx.common.model.request.OrderRequest;
import com.wx.service.ShopService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/shop/order")
public class ShopOrderController {
    private final ShopService shopService;

    public ShopOrderController(ShopService shopService) {
        this.shopService = shopService;
    }

    // 用户发起预退款, 后台确认退款
    @RequestMapping(value = "/refund", method = {RequestMethod.POST})
    public ApiResponse<Boolean> confirmRefund(@RequestBody OrderRequest request) {
        try {
            Boolean updated = shopService.prepareRefund(request);
            return updated ? ApiResponse.success(true) : ApiResponse.fail(500, "update error");
        } catch (Exception e) {
            return ApiResponse.fail(400, "更新订单状态发生错误");
        }
    }
}
