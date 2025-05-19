package com.wx.service;

import com.wx.common.model.request.ProductRequest;

import java.util.List;

public interface ProductService {

    List<String> queryProductCategory(ProductRequest request);

    List<String> queryProductBrand(ProductRequest request);
}
