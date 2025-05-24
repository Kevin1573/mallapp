package com.wx.admin.controller;

import com.wx.common.exception.BizException;
import com.wx.common.model.ApiResponse;
import com.wx.common.model.OrderListItem;
import com.wx.common.model.PageResponse;
import com.wx.common.model.request.OrderListRequest;
import com.wx.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderListController {

    private final OrderService orderService;

    public OrderListController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/list")
    public PageResponse<OrderListItem> orderList(@RequestHeader("Authorization") String authHeader,
                                                 @RequestBody OrderListRequest request) {
        if (StringUtils.isNotBlank(authHeader) && StringUtils.isBlank(request.getToken())) {
            request.setToken(authHeader);
        }
        if (StringUtils.isBlank(request.getToken())) {
            throw new BizException("用户没有登录, 或者token 失效");
        }
        return ApiResponse.page(orderService.queryOrderList(request));
    }
}
