package com.wx.service;

import com.wx.common.model.request.ProductRequest;

import java.util.List;
import java.util.Map;

public interface ProductService {

    List<String> queryProductCategory(ProductRequest request);

    List<Map<String, String>> queryProductBrand(ProductRequest request);
}
