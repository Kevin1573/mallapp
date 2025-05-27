package com.wx.service.impl;

import com.alibaba.fastjson.JSON;
import com.wx.common.model.ShopConfigResponse;
import com.wx.common.model.request.BestSellingGoodsRequest;
import com.wx.common.model.request.ShopConfigRequest;
import com.wx.common.model.request.ShopRebateRequest;
import com.wx.common.model.response.ShopConfigDOResponse;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.entity.RebateDO;
import com.wx.orm.entity.ShopConfigDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.orm.mapper.RebateMapper;
import com.wx.orm.mapper.ShopConfigMapper;
import com.wx.service.GoodsService;
import com.wx.service.RebateService;
import com.wx.service.ShopService;
import com.wx.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService {
    private final ShopConfigMapper shopConfigMapper;
    private final TokenService tokenService;
    private final RebateMapper rebateMapper;
    private final GoodsService goodsService;
    private final RebateService rebateService;

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
        List<Map<String, String>> bestSellingGoodsList = new ArrayList<>(goodsDOS.size());
        for (GoodsDO goodsDO : goodsDOS) {
            Map<String, String> bestSellingGoods = new HashMap<>();
            bestSellingGoods.put("id", goodsDO.getId().toString());
            bestSellingGoods.put("name", goodsDO.getName());
            bestSellingGoods.put("goodsPic", goodsDO.getGoodsPic());
            bestSellingGoods.put("price", String.valueOf(goodsDO.getPrice()));
            bestSellingGoods.put("sales", String.valueOf(goodsDO.getSales())); // 销量
            bestSellingGoodsList.add(bestSellingGoods);
        }

        String bestSellingJson = JSON.toJSONString(bestSellingGoodsList);
        ShopConfigDO shopConfigDO = new ShopConfigDO();
        shopConfigDO.setId(request.getId());
        shopConfigDO.setBestSellers(bestSellingJson);
        int updated = shopConfigMapper.updateById(shopConfigDO);
        return updated > 0;
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
}
