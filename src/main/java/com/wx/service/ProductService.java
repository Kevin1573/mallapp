package com.wx.service;

import java.util.List;

public interface ProductService {

    List<String> queryProductCategory(String fromMall);

    List<String> queryProductBrand(String fromMall);
}
