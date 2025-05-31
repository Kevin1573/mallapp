package com.wx.controller;

import com.wx.common.exception.BizException;
import com.wx.common.model.Response;
import com.wx.common.model.request.ProductRequest;
import com.wx.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // 添加产品分类接口
    @PostMapping(value = "/category")
    public Response<List<String>> productCategory(@RequestBody ProductRequest request) {
        try {
            return Response.success(productService.queryProductCategory(request));
        } catch (Exception e) {
            log.error("ProductCategory exception", e);
            return Response.failure("查询分类出错了...");
        }
    }

    @PostMapping(value = "/brand")
    public Response<List<Map<String, String>>> productBrand(@RequestHeader("Authorization") String authHeader,
                                                            @RequestBody ProductRequest request) {
        if (StringUtils.isNotBlank(authHeader)) {
            request.setToken(authHeader);
        }
        if (StringUtils.isBlank(request.getToken())) {
            throw new BizException("用户没有登录, 或者token 失效");
        }
        try {
            return Response.success(productService.queryProductBrand(request));
        } catch (Exception e) {
            log.error("ProductBrand exception", e);
            return Response.failure("查询品牌出错了...");
        }
    }
}
