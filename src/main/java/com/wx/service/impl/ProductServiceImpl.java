package com.wx.service.impl;

import com.wx.orm.mapper.GoodsMapper;
import com.wx.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final GoodsMapper goodsMapper;

    @Override
    public List<String> queryProductCategory() {
        return goodsMapper.queryProductCategory();
    }

    @Override
    public List<String> queryProductBrand() {
        return goodsMapper.queryProductBrand();
    }
}
