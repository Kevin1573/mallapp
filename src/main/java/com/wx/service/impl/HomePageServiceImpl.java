package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.common.model.request.SaveOrUpdateBannerRequest;
import com.wx.common.model.request.SaveOrUpdateModuleRequest;
import com.wx.common.model.response.SaveOrUpdateModuleResponse;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.entity.HomePageBannerDO;
import com.wx.orm.entity.HomePageModuleDO;
import com.wx.orm.mapper.GoodsMapper;
import com.wx.orm.mapper.HomePageBannerMapper;
import com.wx.orm.mapper.HomePageModuleMapper;
import com.wx.service.HomePageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class HomePageServiceImpl implements HomePageService {

    @Autowired
    private HomePageBannerMapper homePageBannerMapper;
    @Autowired
    private HomePageModuleMapper homePageModuleMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public void saveOrUpdateBanner(SaveOrUpdateBannerRequest request) {
        HomePageBannerDO homePageBannerDO = homePageBannerMapper.selectOne(new LambdaQueryWrapper<>());
        if (Objects.isNull(homePageBannerDO)) {
            HomePageBannerDO newBannerDO = new HomePageBannerDO();
            newBannerDO.setBanner(request.getBanner());
            newBannerDO.setCreateTime(new Date());
            newBannerDO.setModifyTime(new Date());
            homePageBannerMapper.insert(newBannerDO);
        } else {
            homePageBannerDO.setBanner(request.getBanner());
            homePageBannerDO.setModifyTime(new Date());
            homePageBannerMapper.updateById(homePageBannerDO);
        }
    }

    @Override
    public String queryBanner() {
        return homePageBannerMapper.selectOne(new LambdaQueryWrapper<>()).getBanner();
    }

    @Override
    public void saveOrUpdateModule(SaveOrUpdateModuleRequest request) {
        if (Objects.isNull(request.getId())) {
            HomePageModuleDO homePageModuleDO = new HomePageModuleDO();
            homePageModuleDO.setTitle(request.getTitle());
            homePageModuleDO.setBanner(request.getBanner());
            homePageModuleDO.setGoodsList(request.getGoodsList());
            homePageModuleDO.setCreateTime(new Date());
            homePageModuleDO.setModifyTime(new Date());
            homePageModuleMapper.insert(homePageModuleDO);
        } else {
            HomePageModuleDO homePageModuleDO = homePageModuleMapper.selectById(request.getId());
            homePageModuleDO.setTitle(request.getTitle());
            homePageModuleDO.setBanner(request.getBanner());
            homePageModuleDO.setGoodsList(request.getGoodsList());
            homePageModuleDO.setModifyTime(new Date());
            homePageModuleMapper.updateById(homePageModuleDO);
        }
    }

    @Override
    public List<SaveOrUpdateModuleResponse> queryModule() {
        LambdaQueryWrapper<HomePageModuleDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(HomePageModuleDO::getId);
        List<HomePageModuleDO> homePageModuleDOS = homePageModuleMapper.selectList(queryWrapper);
        List<SaveOrUpdateModuleResponse> responseList = new ArrayList<>();
        for (HomePageModuleDO homePageModuleDO : homePageModuleDOS) {
            SaveOrUpdateModuleResponse response = new SaveOrUpdateModuleResponse();
            BeanUtils.copyProperties(homePageModuleDO, response);
            String goodsList = homePageModuleDO.getGoodsList();
            LambdaQueryWrapper<GoodsDO> goodsQuery = new LambdaQueryWrapper<>();
            List<GoodsDO> goodsDOS = new ArrayList<>();
            if (!StringUtils.isEmpty(goodsList)) {
                goodsQuery.in(GoodsDO::getId, Arrays.asList(goodsList.split(",")));
                goodsDOS = goodsMapper.selectList(goodsQuery);
            }
            response.setGoodsList(goodsDOS);
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public void deleteModuleById(SaveOrUpdateModuleRequest request) {
        homePageModuleMapper.deleteById(request.getId());
    }


}
