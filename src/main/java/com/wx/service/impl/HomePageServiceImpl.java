package com.wx.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wx.common.model.request.HomePageRequest;
import com.wx.common.model.request.QueryGoodsByIdRequest;
import com.wx.common.model.request.ShopModel;
import com.wx.common.model.response.CompanyConfigResponse;
import com.wx.common.model.response.HomePageResponse;
import com.wx.common.model.response.QueryRecondDetailByUnitResponse;
import com.wx.dto.BestSellingGoods;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.entity.ShopCombinationRecommendationDO;
import com.wx.orm.entity.ShopConfigDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.orm.mapper.GoodsMapper;
import com.wx.orm.mapper.ShopCombinationRecommendationMapper;
import com.wx.orm.mapper.ShopConfigMapper;
import com.wx.service.HomePageService;
import com.wx.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HomePageServiceImpl implements HomePageService {

    @Autowired
    private ShopConfigMapper shopConfigMapper;
    @Autowired
    private ShopCombinationRecommendationMapper recommendationMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private TokenService tokenService;

    @Override
    public HomePageResponse queryPage(HomePageRequest request) {
        // 查询基本信息
        LambdaQueryWrapper<ShopConfigDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShopConfigDO::getFromMall, request.getFrom());
        ShopConfigDO shopConfigDO = shopConfigMapper.selectOne(queryWrapper);
        HomePageResponse response = new HomePageResponse();
        BeanUtils.copyProperties(shopConfigDO, response);

        // 查询热销商品列表
        List<ShopModel> sellersModels = new ArrayList<>();
        String bestSellers = shopConfigDO.getBestSellers();
        if (StringUtils.isEmpty(bestSellers)) {
            return response;
        }
        List<BestSellingGoods> bestSellingGoods = JSON.parseArray(bestSellers, BestSellingGoods.class);

        for (BestSellingGoods goodsId : bestSellingGoods) {
            GoodsDO goodsDO = goodsMapper.selectById(goodsId.getId());
            ShopModel model = new ShopModel();
            model.setId(goodsDO.getId());
            model.setPic(goodsDO.getGoodsPic());
            model.setTitle(goodsDO.getGoodsTitle());
            model.setName(goodsDO.getName());
            model.setPrice(goodsDO.getPrice());
            model.setGoodsUnit(goodsDO.getGoodsUnit());
            sellersModels.add(model);
        }
        response.setBestSellersModels(sellersModels);

        // 查询品牌列表
        List<ShopModel> brandModels = new ArrayList<>();
        QueryWrapper<GoodsDO> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("DISTINCT (brand), brand_pic");
        queryWrapper1.eq("from_mall", request.getFrom());
        queryWrapper1.eq("first_goods", true);
        List<GoodsDO> goodsDOS = goodsMapper.selectList(queryWrapper1);

        Map<String, String> brandMap = new HashMap<>();
        for (GoodsDO goodsDO : goodsDOS) {
            brandMap.putIfAbsent(goodsDO.getBrand(), goodsDO.getBrandPic());
        }

        brandMap.forEach((k, v) -> {
            ShopModel model = new ShopModel();
            model.setPic(v);
            model.setName(k);
            brandModels.add(model);
        });

        response.setBrandModels(brandModels);

        // 查询组合推荐 recommendGoods == 2
        List<ShopModel> reconModels = new ArrayList<>();
        LambdaQueryWrapper<GoodsDO> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(GoodsDO::getFromMall, request.getFrom());
        queryWrapper2.eq(GoodsDO::getFirstGoods, true);
        queryWrapper2.eq(GoodsDO::getRecommendGoods, 2);

        List<GoodsDO> shopCombinationRecommendationDOS = goodsMapper.selectList(queryWrapper2);
        for (GoodsDO recommendationDO : shopCombinationRecommendationDOS) {
            ShopModel model = new ShopModel();
            model.setId(recommendationDO.getId());
            model.setPic(recommendationDO.getGoodsPic());
            model.setTitle(recommendationDO.getGoodsTitle());
            model.setName(recommendationDO.getName());
            model.setPrice(recommendationDO.getPrice());
            model.setGoodsUnit(recommendationDO.getGoodsUnit());
            reconModels.add(model);
        }
        response.setReconModels(reconModels);

        return response;
    }

    @Override
    public CompanyConfigResponse queryCompanyConfig(HomePageRequest request) {
        LambdaQueryWrapper<ShopConfigDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShopConfigDO::getFromMall, request.getFrom());
        ShopConfigDO shopConfigDO = shopConfigMapper.selectOne(queryWrapper);
        CompanyConfigResponse response = new CompanyConfigResponse();
        BeanUtils.copyProperties(shopConfigDO, response);
        return response;
    }

    @Override
    public List<QueryRecondDetailByUnitResponse> queryRecondDetail(QueryGoodsByIdRequest request) {
        UserProfileDO userByToken = tokenService.getUserByToken(request.getToken());

        LambdaQueryWrapper<ShopCombinationRecommendationDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShopCombinationRecommendationDO::getGoodsUnit, request.getGoodsUnit());
        List<ShopCombinationRecommendationDO> recommendationDOS = recommendationMapper.selectList(queryWrapper);

        List<QueryRecondDetailByUnitResponse> responseList = new ArrayList<>();
        for (ShopCombinationRecommendationDO recondDO : recommendationDOS) {
            QueryRecondDetailByUnitResponse response = new QueryRecondDetailByUnitResponse();
            BeanUtils.copyProperties(recondDO, response);
            responseList.add(response);
        }
        return responseList;
    }
}
