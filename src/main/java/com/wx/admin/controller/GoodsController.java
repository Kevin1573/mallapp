package com.wx.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.common.exception.BizException;
import com.wx.common.model.ApiResponse;
import com.wx.common.model.PageResponse;
import com.wx.common.model.request.GoodsRequest;
import com.wx.common.model.response.GoodsDOResponse;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.service.GoodsService;
import com.wx.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    private final GoodsService goodsService;
    private final TokenService tokenService;

    public GoodsController(GoodsService goodsService, TokenService tokenService) {
        this.goodsService = goodsService;
        this.tokenService = tokenService;
    }

    /**
     * 分页查询商品
     */
    @PostMapping("/find")
    public PageResponse<GoodsDOResponse> findGoods(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody GoodsRequest request) {
        if (StringUtils.isNotBlank(authHeader)) {
            request.setToken(authHeader);
        }
        if (StringUtils.isBlank(request.getToken())) {
            throw new BizException("用户没有登录, 或者token 失效");
        }

        UserProfileDO userByToken = tokenService.getUserByToken(request.getToken());
        if (userByToken == null) {
            throw new BizException("用户没有登录, 或者token 失效");
        }
        if ("normal".equals(userByToken.getSource())) {
            return ApiResponse.failPage(400, "用户没有登录, 或者token 失效");
        }

        QueryWrapper<GoodsDO> wrapper = new QueryWrapper<>();
        String source = userByToken.getSource();
        if (StringUtils.isNotBlank(source) && "shopOwner".equals(source)) {
            wrapper.eq("from_mall", userByToken.getFromShopName());
        }
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
        wrapper.eq("first_goods", true);


        Page<GoodsDO> page = new Page<>(request.getPage(), request.getPageSize());
        Page<GoodsDO> pageResult = goodsService.page(page, wrapper);
        List<GoodsDO> records = pageResult.getRecords();
        List<GoodsDOResponse> goodsDOResponses = new ArrayList<>();
        // 根据商品规格specifications进行分组
        for (GoodsDO record : records) {
            String goodsUnit = record.getGoodsUnit();
            QueryChainWrapper<GoodsDO> doQueryChainWrapper = goodsService.query()
                    .eq("goods_unit", goodsUnit);
            List<GoodsDO> goodsDOList = doQueryChainWrapper.list();

            GoodsDOResponse goodsDOItem = new GoodsDOResponse();
            goodsDOItem.setId(record.getId());
            goodsDOItem.setName(record.getName());
            goodsDOItem.setDescription(record.getDescription());
            goodsDOItem.setGoodsPic(record.getGoodsPic());
            goodsDOItem.setGoodsTitle(record.getGoodsTitle());
            goodsDOItem.setBrand(record.getBrand());
            goodsDOItem.setCategory(record.getCategory());
            goodsDOItem.setGoodsUnit(record.getGoodsUnit());
            goodsDOItem.setFirstGoods(record.getFirstGoods());
            goodsDOItem.setFromMall(record.getFromMall());
            goodsDOItem.setCreateTime(record.getCreateTime());
            goodsDOItem.setStatus(record.getStatus());
            goodsDOItem.setExt(record.getExt());

            List<GoodsDOResponse.GoodsSubDO> goodsSubDOS = goodsDOList.stream().map(res -> {
                GoodsDOResponse.GoodsSubDO goodsSubDO = new GoodsDOResponse.GoodsSubDO();
                goodsSubDO.setFirstGoods(res.getFirstGoods());
                goodsSubDO.setPrice(res.getPrice());
                goodsSubDO.setGoodsPic(res.getGoodsPic());
                goodsSubDO.setGoodsTitle(res.getGoodsTitle());
                goodsSubDO.setInventory(res.getInventory());
                goodsSubDO.setSales(res.getSales());
                goodsSubDO.setSpecifications(res.getSpecifications());
                goodsSubDO.setExt(res.getExt());
                goodsSubDO.setId(res.getId());
                return goodsSubDO;
            }).collect(Collectors.toList());
            goodsDOItem.setSubGoodsList(goodsSubDOS);
            goodsDOResponses.add(goodsDOItem);
        }

        Page<GoodsDOResponse> objectPage = Page.of(pageResult.getCurrent(), pageResult.getSize(), pageResult.getTotal());
        objectPage.setRecords(goodsDOResponses);

        return ApiResponse.page(objectPage);
    }

    /**
     * 新增商品
     */
    @PostMapping("/add")
    public ApiResponse<Boolean> addGoods(@RequestBody GoodsDO goods) {
        try {
            // 设置创建时间
            goods.setCreateTime(new Date());

            if (StringUtils.isEmpty(goods.getGoodsUnit())) {
                goods.setGoodsUnit(UUID.randomUUID().toString());
                goods.setFirstGoods(true);
            } else {
                // 查询相同goodsUnit的商品
                QueryWrapper<GoodsDO> wrapper = new QueryWrapper<>();
                wrapper.eq("goods_unit", goods.getGoodsUnit());
                wrapper.eq("first_goods", true);
                GoodsDO goodsDO = goodsService.getOne(wrapper);
                if (goodsDO != null) {
                    goods.setId(null);
                    goods.setName(goodsDO.getName());
                    goods.setCategory(goodsDO.getCategory());
                    goods.setBrand(goodsDO.getBrand());
                    goods.setDescription(goodsDO.getDescription());
                    goods.setFirstGoods(false);
                    goods.setFromMall(goodsDO.getFromMall());
                    goods.setCreateTime(new Date());
                    goods.setStatus(goodsDO.getStatus());
                    goods.setInventory(0);
                    goods.setSales(0L);
                    goods.setGoodsUnit(goodsDO.getGoodsUnit());
                }
            }

            // 参数校验
            if (StringUtils.isBlank(goods.getName())) {
                return ApiResponse.fail(400, "商品名称不能为空");
            }

            // 商品名称不能重复
            QueryWrapper<GoodsDO> wrapper = new QueryWrapper<>();
            wrapper.eq("name", goods.getName());
            wrapper.eq("from_mall", goods.getFromMall());
            GoodsDO goodsDO = goodsService.getOne(wrapper);
            if (goodsDO != null) {
                if (goodsDO.getSpecifications() != null
                        && goodsDO.getSpecifications().equals(goods.getSpecifications())) {
                    return ApiResponse.fail(400, "商品规格不能重复");
                }
            }

            if (goods.getPrice() == null || goods.getPrice() <= 0) {
                return ApiResponse.fail(400, "商品价格必须大于0");
            }
            if (goods.getInventory() < 0) {
                return ApiResponse.fail(400, "库存不能为负数");
            }

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
