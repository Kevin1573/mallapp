package com.wx.controller;

import com.wx.common.model.Response;
import com.wx.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // 添加产品分类接口
    @RequestMapping(value = "/category")
    public Response<List<String>> productCategory() {
        return Response.success(productService.queryProductCategory());
    }

    @RequestMapping(value = "/brand")
    public Response<List<String>> productBrand() {
        return Response.success(productService.queryProductBrand());
    }
}
