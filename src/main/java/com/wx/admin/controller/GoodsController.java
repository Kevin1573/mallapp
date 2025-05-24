package com.wx.admin.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.common.model.ApiResponse;
import com.wx.common.model.PageResponse;
import com.wx.common.model.request.GoodsRequest;
import com.wx.orm.entity.GoodsDO;
import com.wx.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    /**
     * 分页查询商品
     */
    @PostMapping("/find")
    public PageResponse<GoodsDO> findGoods(@RequestBody GoodsRequest request) {
        QueryWrapper<GoodsDO> wrapper = new QueryWrapper<>();

        // 按商品名称模糊查询
        if (StringUtils.isNotBlank(request.getName())) {
            wrapper.like("name", request.getName());
        }

        // 按分类过滤
        if (StringUtils.isNotBlank(request.getCategory())) {
            wrapper.eq("category", request.getCategory());
        }

        // 按品牌过滤
        if (StringUtils.isNotBlank(request.getBrand())) {
            wrapper.eq("brand", request.getBrand());
        }

        Page<GoodsDO> page = new Page<>(request.getPage(), request.getPageSize());
        return ApiResponse.page(goodsService.page(page, wrapper));
    }

    /**
     * 新增商品
     */
    @PostMapping("/add")
    public ApiResponse<Boolean> addGoods(@RequestBody GoodsDO goods) {
        try {
            // 参数校验
            if (StringUtils.isBlank(goods.getName())) {
                return ApiResponse.fail(400, "商品名称不能为空");
            }
            if (goods.getPrice() == null || goods.getPrice() <= 0) {
                return ApiResponse.fail(400, "商品价格必须大于0");
            }
            if (goods.getInventory() < 0) {
                return ApiResponse.fail(400, "库存不能为负数");
            }

            // 设置创建时间
            goods.setCreateTime(new Date());
//            goods.setFromMall("mallapp");

            System.out.println(JSON.toJSONString(goods));
            boolean result = goodsService.save(goods);

            return result ? ApiResponse.success(true) : ApiResponse.fail(500, "创建失败");
        } catch (Exception e) {
            return ApiResponse.fail(500, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 更新商品
     */
    @PostMapping("/update")
    public ApiResponse<Boolean> updateGoods(@RequestBody GoodsDO goods) {
        try {
            // 参数校验
            if (goods.getId() == null) {
                return ApiResponse.fail(400, "商品ID不能为空");
            }

            // 校验商品存在性
            GoodsDO existing = goodsService.getById(goods.getId());
            if (existing == null) {
                return ApiResponse.fail(404, "商品不存在");
            }

            // 价格校验
            if (goods.getPrice() != null && goods.getPrice() <= 0) {
                return ApiResponse.fail(400, "价格必须大于0");
            }

            // 库存校验
            if (goods.getInventory() < 0) {
                return ApiResponse.fail(400, "库存不能为负数");
            }

            // 设置更新时间
            goods.setModifyTime(new Date());
            boolean result = goodsService.updateById(goods);

            return result ? ApiResponse.success(true) : ApiResponse.fail(500, "更新失败");
        } catch (Exception e) {
            return ApiResponse.fail(500, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 删除商品
     */
    @PostMapping("/del")
    public ApiResponse<Boolean> deleteGoods(@RequestBody GoodsRequest request) {
        try {
            if (request.getId() == null) {
                return ApiResponse.fail(400, "商品ID不能为空");
            }
            return ApiResponse.success(goodsService.removeById(request.getId()));
        } catch (Exception e) {
            return ApiResponse.fail(500, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 商品详情
     */
    @PostMapping("/detail")
    public ApiResponse<GoodsDO> getGoodsDetail(@RequestBody GoodsRequest request) {
        if (request.getId() == null) {
            return ApiResponse.fail(400, "商品ID不能为空");
        }
        return ApiResponse.success(goodsService.getById(request.getId()));
    }

    // 新增状态修改接口
    @PostMapping("/updateStatus")
    public ApiResponse<Boolean> updateStatus(@RequestBody GoodsRequest request) {
        // 实现状态切换逻辑
        return ApiResponse.success(true);
    }

    // 添加图片上传接口
    @PostMapping("/uploadImage")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 实现图片上传逻辑
        return ApiResponse.success("图片上传成功");
    }
}
