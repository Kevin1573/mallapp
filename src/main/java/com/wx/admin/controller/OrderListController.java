package com.wx.admin.controller;

import com.wx.common.enums.OrderStatus;
import com.wx.common.exception.BizException;
import com.wx.common.model.ApiResponse;
import com.wx.common.model.OrderListItem;
import com.wx.common.model.PageResponse;
import com.wx.common.model.request.OrderListRequest;
import com.wx.orm.entity.UserProfileDO;
import com.wx.service.OrderService;
import com.wx.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderListController {

    private final OrderService orderService;
    private final TokenService tokenService;

    public OrderListController(OrderService orderService, TokenService tokenService) {
        this.orderService = orderService;
        this.tokenService = tokenService;
    }

    @PostMapping("/list")
    public PageResponse<OrderListItem> orderList(@RequestHeader("Authorization") String authHeader,
                                                 @RequestBody OrderListRequest request) {
        if (StringUtils.isNotBlank(authHeader)) {
            request.setToken(authHeader);
        }
        if (StringUtils.isBlank(request.getToken())) {
            throw new BizException("用户没有登录, 或者token 失效");
        }

        UserProfileDO userByToken = tokenService.getUserByToken(request.getToken());
        if (userByToken == null) {
            throw new BizException("用户没有登录, 或者token 失效");
        }
        if ("normal".equals(userByToken.getSource())) {
            return ApiResponse.failPage(400, "用户没有登录, 或者token 失效");
        }

        return ApiResponse.page(orderService.queryOrderList(request, userByToken));
    }

    @PostMapping("/updateState")
    public ApiResponse<Boolean> updateState(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody OrderListRequest request) {
        if (StringUtils.isNotBlank(authHeader)) {
            request.setToken(authHeader);
        }
        if (StringUtils.isBlank(request.getToken())) {
            throw new BizException("用户没有登录, 或者token 失效");
        }

        UserProfileDO userByToken = tokenService.getUserByToken(request.getToken());
        if (userByToken == null) {
            throw new BizException("用户没有登录, 或者token 失效");
        }
        if ("normal".equals(userByToken.getSource())) {
            return ApiResponse.fail(400, "用户没有登录, 或者token 失效");
        }
        // 校验 订单状态 是否在enums OrderStatus 中

        OrderStatus orderStatus = OrderStatus.fromCode(request.getStatus());
        if (orderStatus == null) {
            return ApiResponse.fail(400, "订单状态不正确");
        }
        int updated = orderService.updateOrderStatus(request.getTradeNo(), orderStatus);
        return ApiResponse.success(updated > 0);
    }
}
