package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wx.common.model.request.ProductRequest;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.mapper.GoodsMapper;
import com.wx.service.ProductService;
import com.wx.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final GoodsMapper goodsMapper;
    private final TokenService tokenService;

    @Override
    public List<String> queryProductCategory(ProductRequest request) {
        List<String> list = new ArrayList<>();
        QueryWrapper<GoodsDO> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("DISTINCT (category)");
        queryWrapper1.eq("from_mall", request.getFrom());
        List<GoodsDO> goodsDOS = goodsMapper.selectList(queryWrapper1);
        for (GoodsDO goodsDO : goodsDOS) {
           list.add(goodsDO.getCategory());
        }
        return list;
    }

    @Override
    public List<Map<String, String>> queryProductBrand(ProductRequest request) {
//        UserProfileDO userByToken = tokenService.getUserByToken(request.getToken());
        if (StringUtils.isEmpty(request.getFrom())) {
            throw new RuntimeException("店铺标识不能为空");
        }
        List<Map<String, String>> list = new ArrayList<>();

        QueryWrapper<GoodsDO> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("DISTINCT (brand), brand_pic");
        queryWrapper1.eq("from_mall", request.getFrom());
        queryWrapper1.eq("first_goods", true);
        List<GoodsDO> goodsDOS = goodsMapper.selectList(queryWrapper1);
        for (GoodsDO goodsDO : goodsDOS) {
            Map<String, String> map = new HashMap<>();
            map.put("brand", goodsDO.getBrand());
            map.put("brand_pic", goodsDO.getBrandPic());
            list.add(map);
        }
        return list;
    }
}
