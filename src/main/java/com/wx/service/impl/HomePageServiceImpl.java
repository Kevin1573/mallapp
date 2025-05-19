package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wx.common.model.request.HomePageRequest;
import com.wx.common.model.request.ShopModel;
import com.wx.common.model.response.CompanyConfigResponse;
import com.wx.common.model.response.HomePageResponse;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.entity.ShopCombinationRecommendationDO;
import com.wx.orm.entity.ShopConfigDO;
import com.wx.orm.mapper.GoodsMapper;
import com.wx.orm.mapper.ShopCombinationRecommendationMapper;
import com.wx.orm.mapper.ShopConfigMapper;
import com.wx.service.HomePageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HomePageServiceImpl implements HomePageService {

    @Autowired
    private ShopConfigMapper shopConfigMapper;
    @Autowired
    private ShopCombinationRecommendationMapper recommendationMapper;
    @Autowired
    private GoodsMapper goodsMapper;

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
        List<String> sellersList = Arrays.asList(bestSellers.split(bestSellers));
        for (String goodsId : sellersList) {
            GoodsDO goodsDO = goodsMapper.selectById(goodsId);
            ShopModel model = new ShopModel();
            model.setPic(goodsDO.getGoodsPic());
            model.setName(goodsDO.getName());
            model.setPrice(goodsDO.getPrice());
            sellersModels.add(model);
        }
        response.setBestSellersModels(sellersModels);

        // 查询品牌列表
        List<ShopModel> brandModels = new ArrayList<>();
        QueryWrapper<GoodsDO> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("DISTINCT (brand), brand_pic");
        queryWrapper1.eq("from_mall", request.getFrom());
        List<GoodsDO> goodsDOS = goodsMapper.selectList(queryWrapper1);
        for (GoodsDO goodsDO : goodsDOS) {
            ShopModel model = new ShopModel();
            model.setPic(goodsDO.getBrandPic());
            model.setName(goodsDO.getBrand());
            brandModels.add(model);
        }
        response.setBrandModels(brandModels);

        // 查询组合推荐
        List<ShopModel> reconModels = new ArrayList<>();
        LambdaQueryWrapper<ShopCombinationRecommendationDO> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(ShopCombinationRecommendationDO::getFromMall, request.getFrom());
        queryWrapper2.eq(ShopCombinationRecommendationDO::getFirstGoods, true);
        List<ShopCombinationRecommendationDO> shopCombinationRecommendationDOS = recommendationMapper.selectList(queryWrapper2);
        for (ShopCombinationRecommendationDO recommendationDO : shopCombinationRecommendationDOS) {
            ShopModel model = new ShopModel();
            model.setPic(recommendationDO.getGoodsPic());
            model.setName(recommendationDO.getName());
            model.setPrice(recommendationDO.getPrice());
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
}
