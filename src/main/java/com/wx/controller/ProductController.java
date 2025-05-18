package com.wx.controller;

import com.wx.common.model.Response;
import com.wx.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // 添加产品分类接口
    @PostMapping(value = "/category")
    public Response<List<String>> productCategory() {
        try {
            return Response.success(productService.queryProductCategory());
        } catch (Exception e) {
            log.error("ProductCategory exception", e);
            return Response.failure("查询分类出错了...");
        }
    }

    @PostMapping(value = "/brand")
    public Response<List<String>> productBrand() {
        try {
            return Response.success(productService.queryProductBrand());
        } catch (Exception e) {
            log.error("ProductCategory exception", e);
            return Response.failure("查询品牌出错了...");
        }
    }
}
