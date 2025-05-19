package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wx.common.model.request.ProductRequest;
import com.wx.common.model.request.ShopModel;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.mapper.GoodsMapper;
import com.wx.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final GoodsMapper goodsMapper;

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
    public List<String> queryProductBrand(ProductRequest request) {
        List<String> list = new ArrayList<>();
        QueryWrapper<GoodsDO> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("DISTINCT (brand), brand_url");
        queryWrapper1.eq("from_mall", request.getFrom());
        List<GoodsDO> goodsDOS = goodsMapper.selectList(queryWrapper1);
        for (GoodsDO goodsDO : goodsDOS) {
            list.add(goodsDO.getBrand());
        }
        return list;
    }
}
