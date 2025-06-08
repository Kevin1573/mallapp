package com.wx.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.wx.common.exception.BizException;
import com.wx.common.model.ApiResponse;
import com.wx.common.model.request.BestSellingGoodsRequest;
import com.wx.common.model.request.RecommendedGoodsRequest;
import com.wx.common.model.request.ShopConfigRequest;
import com.wx.common.model.response.ShopConfigDOResponse;
import com.wx.orm.entity.GoodsDO;
import com.wx.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

    @RequestMapping(value = "/updateBestSellingGoods", method = {RequestMethod.POST})
    public ApiResponse<Boolean> updateBestSellingGoods(@RequestBody BestSellingGoodsRequest request,
                                                       @RequestHeader("Authorization") String authHeader) {
        try {
            if (StringUtils.isNotBlank(authHeader) && StringUtils.isBlank(request.getToken())) {
                request.setToken(authHeader);
            }
            if (StringUtils.isBlank(request.getToken())) {
                throw new BizException("用户没有登录, 或者token 失效");
            }

            Boolean updated = shopService.updateBestSellingGoods(request);
            return updated ? ApiResponse.success(true) : ApiResponse.fail(500, "update error");
        } catch (Exception e) {
            log.error("updateBestSellingGoods is error , ",  e);
            return ApiResponse.fail(400, "updateBestSellingGoods is error , " + e.getMessage());
        }
    }

    @RequestMapping(value = "/getBestSellingList", method = {RequestMethod.POST})
    public ApiResponse<List<GoodsDO>> getBestSellingList(@RequestBody BestSellingGoodsRequest request,
                                                         @RequestHeader("Authorization") String authHeader) {
        try {
            if (StringUtils.isNotBlank(authHeader) && StringUtils.isBlank(request.getToken())) {
                request.setToken(authHeader);
            }
            if (StringUtils.isBlank(request.getToken())) {
                throw new BizException("用户没有登录, 或者token 失效");
            }

            List<GoodsDO> shopConfigList = shopService.selectListByFromMall(request.getFromMall());
            return ApiResponse.success(shopConfigList);
        } catch (Exception e) {
            return ApiResponse.fail(400, "updateBestSellingGoods is error , " + e.getMessage());
        }
    }

    // updateRecommendedGoods
    @RequestMapping(value = "/updateRecommendedGoods", method = {RequestMethod.POST})
    public ApiResponse<Boolean> updateRecommendedGoods(@RequestBody RecommendedGoodsRequest request,
                                                       @RequestHeader("Authorization") String authHeader) {
        try {
            if (StringUtils.isNotBlank(authHeader) && StringUtils.isBlank(request.getToken())) {
                request.setToken(authHeader);
            }
            if (StringUtils.isBlank(request.getToken())) {
                throw new BizException("用户没有登录, 或者token 失效");
            }
            Boolean updated = shopService.updateRecommendedGoods(request);
            return updated ? ApiResponse.success(true) : ApiResponse.fail(500, "update error");
        } catch (Exception e) {
            return ApiResponse.fail(400, "updateBestSellingGoods is error , " + e.getMessage());
        }
    }

    // getRecommendedGoodsList
    @RequestMapping(value = "/getRecommendedGoodsList", method = {RequestMethod.POST})
    public ApiResponse<JSONObject> getRecommendedGoodsList(@RequestBody BestSellingGoodsRequest request,
                                                           @RequestHeader("Authorization") String authHeader) {
        try {
            if (StringUtils.isNotBlank(authHeader) && StringUtils.isBlank(request.getToken())) {
                request.setToken(authHeader);
            }
            if (StringUtils.isBlank(request.getToken())) {
                throw new BizException("用户没有登录, 或者token 失效");
            }
            JSONObject shopConfigList = shopService.selectRecommendListByFromMall(request.getFromMall());
            return ApiResponse.success(shopConfigList);
        } catch (Exception e) {
            return ApiResponse.fail(400, "updateBestSellingGoods is error , " + e.getMessage());
        }
    }


}
