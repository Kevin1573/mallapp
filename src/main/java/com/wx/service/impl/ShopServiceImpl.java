package com.wx.service.impl;

import com.alibaba.fastjson.JSON;
import com.wx.common.model.ShopConfigResponse;
import com.wx.common.model.request.BestSellingGoodsRequest;
import com.wx.common.model.request.ShopConfigRequest;
import com.wx.common.model.request.ShopRebateRequest;
import com.wx.common.model.response.ShopConfigDOResponse;
import com.wx.dto.BestSellingGoods;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.entity.RebateDO;
import com.wx.orm.entity.ShopConfigDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.orm.mapper.RebateMapper;
import com.wx.orm.mapper.ShopConfigMapper;
import com.wx.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService {
    private final ShopConfigMapper shopConfigMapper;
    private final TokenService tokenService;
    private final RebateMapper rebateMapper;
    private final GoodsService goodsService;
    private final RebateService rebateService;
    private final OrderService orderService;

    @Override
    public ShopConfigResponse getShopConfigInfo(ShopConfigRequest request) {
        UserProfileDO userProfile = tokenService.getUserByToken(request.getToken());
        ShopConfigDO shopConfigDO = shopConfigMapper.selectByFrom(request.getFrom());
        RebateDO rebateDO = rebateMapper.selectById(userProfile.getPosition());
        return new ShopConfigResponse(shopConfigDO.getShopName(),
                shopConfigDO.getFreight(), rebateDO.getRatio());
    }

    @Override
    public ShopConfigDOResponse getShopInfo(ShopConfigRequest request) {
        String token = request.getToken();
        UserProfileDO userByToken = tokenService.getUserByToken(token);
        String source = userByToken.getSource();
//        String fromShopName = userByToken.getFromShopName();
        ShopConfigDO shopConfigDO = shopConfigMapper.selectByIdOrFromMall(request.getId(), request.getFromMall());
        ShopConfigDOResponse response = new ShopConfigDOResponse();
        BeanUtils.copyProperties(shopConfigDO, response);
        response.setSource(source);
        return response;
    }

    @Override
    public Boolean updateBestSellingGoods(BestSellingGoodsRequest request) {
        String token = request.getToken();
        UserProfileDO userByToken = tokenService.getUserByToken(token);
        if (userByToken == null) return false;

        Long[] bestSellingGoodsIds = request.getBestSellingGoodsIds();
        // 查询出这些商品
        List<GoodsDO> goodsDOS = goodsService.listByIds(Arrays.asList(bestSellingGoodsIds));
        List<BestSellingGoods> bestSellingGoodsList = new ArrayList<>(goodsDOS.size());
        for (GoodsDO goodsDO : goodsDOS) {
            bestSellingGoodsList.add(fromGoodsDO(goodsDO));
        }

        String bestSellingJson = JSON.toJSONString(bestSellingGoodsList);
        ShopConfigDO shopConfigDO = new ShopConfigDO();
        shopConfigDO.setFromMall(request.getFromMall());
        shopConfigDO.setBestSellers(bestSellingJson);
        int updated = shopConfigMapper.updateByFromMall(shopConfigDO);
        return updated > 0;
    }

    public BestSellingGoods fromGoodsDO(GoodsDO goodsDO) {
        BestSellingGoods dto = new BestSellingGoods();
        dto.setId(goodsDO.getId());
        dto.setName(goodsDO.getName());
        dto.setGoodsPic(goodsDO.getGoodsPic());
        dto.setPrice(BigDecimal.valueOf(goodsDO.getPrice()));
        dto.setSales(Math.toIntExact(goodsDO.getSales()));
        return dto;
    }

    @Override
    public void initShopUserLevelDiscount(String fromMall) {
        rebateService.initRebate(fromMall);
    }

    @Override
    public List<RebateDO> getRebateList(ShopConfigRequest request) {
        return rebateService.queryRebateList(request.getFrom());
    }

    @Override
    public Boolean editRebateList(ShopRebateRequest request) {
        // 根据id 修改商铺折扣
        RebateDO rebateDO = new RebateDO();
        rebateDO.setId(request.getId());
        rebateDO.setRatio(request.getRatio());
        rebateDO.setDescription(request.getDescription());
//        rebateDO.setPositionCode(request.getPositionCode());
        int i = rebateMapper.updateById(rebateDO);
        return i > 0;
    }

    @Override
    public List<GoodsDO> selectListByFromMall(String fromMall) {
        List<ShopConfigDO> shopConfigDOS = shopConfigMapper.selectListByFromMall(fromMall);
        ShopConfigDO shopConfigDO = shopConfigDOS.get(0);
        String bestSellers = shopConfigDO.getBestSellers();
        List<GoodsDO> goodsHistoryDOS = new ArrayList<>();
        if (bestSellers != null) {
            List<BestSellingGoods> bestSellingGoods = JSON.parseArray(bestSellers, BestSellingGoods.class);
            for (BestSellingGoods orderId : bestSellingGoods) {
                GoodsDO goodsHistoryDO = orderService.getOrderById(orderId.getId());
                goodsHistoryDOS.add(goodsHistoryDO);
            }
        }

        return goodsHistoryDOS;
    }
}
