package com.wx.controller;

import com.alibaba.fastjson.JSON;
import com.wx.common.model.Response;
import com.wx.common.model.request.*;
import com.wx.common.model.response.*;
import com.wx.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    @RequestMapping(value = "/initData", method = {RequestMethod.POST})
    public Response initData(@RequestBody UploadGoodsRequest request) {
        try {
            adminService.initData();
            return Response.success();
        } catch (Exception e) {
            log.error("Admin initData exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin initData exception");
        }
    }

    @RequestMapping(value = "/uploadGoods", method = {RequestMethod.POST})
    public Response uploadGoods(@RequestBody UploadGoodsRequest request) {
        try {
            adminService.uploadGoods(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin uploadGoods exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin uploadGoods exception");
        }
    }

    @RequestMapping(value = "/queryOrderHistory", method = {RequestMethod.POST})
    public Response<QueryOrderHistoryResponse> queryOrderHistory(@RequestBody QueryOrderHistoryRequest request) {
        try {
            return Response.success(adminService.queryOrderHistory(request));
        } catch (Exception e) {
            log.error("Admin queryOrderHistory exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin queryOrderHistory exception");
        }
    }

    @RequestMapping(value = "/completeOrder", method = {RequestMethod.POST})
    public Response completeOrder(@RequestBody CompleteOrderRequest request) {
        try {
            adminService.completeOrder(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin completeOrder exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin completeOrder exception");
        }
    }

    @RequestMapping(value = "/updateGoodsHisLogistics", method = {RequestMethod.POST})
    public Response updateGoodsHisLogistics(@RequestBody CompleteOrderRequest request) {
        try {
            adminService.updateGoodsHisLogistics(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin updateGoodsHisLogistics exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin updateGoodsHisLogistics exception");
        }
    }

    @RequestMapping(value = "/deleteGoodById", method = {RequestMethod.POST})
    public Response deleteGoodById(@RequestBody DeleteCommunityRequest request) {
        try {
            adminService.deleteGoodById(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin deleteGoodById exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin deleteGoodById exception");
        }
    }

    @RequestMapping(value = "/updateUserPosition", method = {RequestMethod.POST})
    public Response updateUserPosition(@RequestBody UpdateUserPositionRequest request) {
        try {
            adminService.updateUserPosition(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin updateUserPosition exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin updateUserPosition exception");
        }
    }

    @RequestMapping(value = "/updateGoods", method = {RequestMethod.POST})
    public Response updatePrice(@RequestBody UpdateGoodsRequest request) {
        try {
            adminService.updateGoods(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin updateGoods exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin updateGoods exception");
        }
    }

    @RequestMapping(value = "/addGoodsType", method = {RequestMethod.POST})
    public Response addGoodsType(@RequestBody UploadGoodsRequest request) {
        try {
            adminService.addGoodsType(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin addGoodsType exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin updatePrice exception");
        }
    }

    @RequestMapping(value = "/queryGoodsTypeList", method = {RequestMethod.POST})
    public Response<List<GoodsTypeResponse>> queryGoodsTypeList() {
        try {
            return Response.success(adminService.queryGoodsTypeList());
        } catch (Exception e) {
            log.error("Admin queryGoodsTypeList exception ", e);
            return Response.failure("Admin queryGoodsTypeList exception");
        }
    }

    @RequestMapping(value = "/deleteTypeById", method = {RequestMethod.POST})
    public Response deleteTypeById(@RequestBody GoodsTypeResquest request) {
        try {
            adminService.deleteTypeById(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin deleteTypeById exception ", e);
            return Response.failure("Admin deleteTypeById exception");
        }
    }

    @RequestMapping(value = "/queryUserList", method = {RequestMethod.POST})
    public Response<PageQueryMemberResponse> queryUserList(@RequestBody PageQueryUserRequest request) {
        try {
            return Response.success(adminService.queryUserList(request));
        } catch (Exception e) {
            log.error("Admin queryUserList exception ", e);
            return Response.failure("Admin queryUserList exception");
        }
    }

    @RequestMapping(value = "/updateUserRebate", method = {RequestMethod.POST})
    public Response updateUserRebate(@RequestBody updateUserRebateRequest request) {
        try {
            adminService.updateUserRebate(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin updateUserRebate exception ", e);
            return Response.failure("Admin updateUserRebate exception");
        }
    }

    @RequestMapping(value = "/queryUserRebateResponse", method = {RequestMethod.POST})
    public Response<List<UserRebateResponse>> queryUserRebateResponse() {
        try {
            return Response.success(adminService.queryUserRebateResponse());
        } catch (Exception e) {
            log.error("Admin queryUserRebateResponse exception ", e);
            return Response.failure("Admin queryUserRebateResponse exception");
        }
    }

    @RequestMapping(value = "/updateUserPointNew", method = {RequestMethod.POST})
    public Response updateUserPointNew(@RequestBody UserPointNewRequest request) {
        try {
            adminService.updateUserPointNew(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin updateUserPointNew exception ", e);
            return Response.failure("Admin updateUserPointNew exception");
        }
    }

    @RequestMapping(value = "/queryMyPerformance", method = {RequestMethod.POST})
    public Response<QueryMyPerformanceResponse> queryMyPerformance(@RequestBody QueryMyPerformanceRequest request) {
        try {
            return Response.success(adminService.queryMyPerformance(request));
        } catch (Exception e) {
            log.error("Admin queryMyPerformance exception ", e);
            return Response.failure("Admin queryMyPerformance exception");
        }
    }

}
