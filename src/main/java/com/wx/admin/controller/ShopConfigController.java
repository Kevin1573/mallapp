package com.wx.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.common.model.ApiResponse;
import com.wx.common.model.PageResponse;
import com.wx.common.model.Response;
import com.wx.common.model.ShopConfigResponse;
import com.wx.common.model.request.ShopConfigRequest;
import com.wx.orm.entity.ShopConfigDO;
import com.wx.service.ShopConfigService;
import com.wx.service.ShopService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/shop/config")
public class ShopConfigController {
    private final ShopService shopService;
    private final ShopConfigService shopConfigService;

    public ShopConfigController(ShopService shopService, ShopConfigService shopConfigService) {
        this.shopService = shopService;
        this.shopConfigService = shopConfigService;
    }

    @RequestMapping(value = "/getShopConfigInfo", method = {RequestMethod.POST})
    public Response<?> getShopConfigInfo(@RequestBody ShopConfigRequest request) {
        try {
            ShopConfigResponse shopConfigInfo = shopService.getShopConfigInfo(request);
            return Response.success(shopConfigInfo);
        } catch (Exception e) {
            return Response.failure("getShopConfigInfo is error , " + e.getMessage());
        }
    }

    /**
     * 分页查询商铺配置
     */
    @PostMapping("/find")
    public PageResponse<ShopConfigDO> findConfigs(@RequestBody ShopConfigRequest request) {
        QueryWrapper<ShopConfigDO> wrapper = new QueryWrapper<>();

        // 按店铺名称模糊查询
        if (StringUtils.isNotBlank(request.getShopName())) {
            wrapper.like("shop_name", request.getShopName());
        }

        // 按营业状态过滤
//        if (request.getBusinessStatus() != null) {
//            wrapper.eq("business_status", request.getBusinessStatus());
//        }

        Page<ShopConfigDO> page = new Page<>(request.getPage(), request.getPageSize());
        return ApiResponse.page(shopConfigService.page(page, wrapper));
    }

    /**
     * 新增商铺配置
     */
    @PostMapping("/add")
    public ApiResponse<Boolean> addConfig(@RequestBody ShopConfigRequest config) {
        System.out.println(config);
        try {
            // 参数校验
            if (StringUtils.isBlank(config.getShopName())) {
                return ApiResponse.fail(400, "店铺名称不能为空");
            }
            if (StringUtils.isBlank(config.getContactPhone())) {
                return ApiResponse.fail(400, "联系电话不能为空");
            }
            if (!config.getContactPhone().matches("^1[3-9]\\d{9}$")) {
                return ApiResponse.fail(400, "联系电话格式不正确");
            }

            // 设置创建时间
            config.setCreateTime(new Date());
            ShopConfigDO shopConfigDO = new ShopConfigDO();
            BeanUtil.copyProperties(config, shopConfigDO);
            boolean result = shopConfigService.save(shopConfigDO);

            return result ? ApiResponse.success(true) : ApiResponse.fail(500, "创建失败");
        } catch (Exception e) {
            return ApiResponse.fail(500, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 更新商铺配置
     */
    @PostMapping("/update")
    public ApiResponse<Boolean> updateConfig(@RequestBody ShopConfigDO config) {
        try {
            // 参数校验
            if (config.getId() == null) {
                return ApiResponse.fail(400, "配置ID不能为空");
            }

            // 校验配置存在性
            ShopConfigDO existing = shopConfigService.getById(config.getId());
            if (existing == null) {
                return ApiResponse.fail(404, "配置不存在");
            }

            // 联系电话格式校验
            if (StringUtils.isNotBlank(config.getContactPhone())
                    && !config.getContactPhone().matches("^1[3-9]\\d{9}$")) {
                return ApiResponse.fail(400, "联系电话格式不正确");
            }

            // 设置更新时间
            config.setModifyTime(new Date());
            boolean result = shopConfigService.updateById(config);

            return result ? ApiResponse.success(true) : ApiResponse.fail(500, "更新失败");
        } catch (Exception e) {
            return ApiResponse.fail(500, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 删除商铺配置
     */
    @PostMapping("/del")
    public ApiResponse<Boolean> deleteConfig(@RequestBody ShopConfigRequest request) {
        try {
            if (request.getId() == null) {
                return ApiResponse.fail(400, "配置ID不能为空");
            }
            return ApiResponse.success(shopConfigService.removeById(request.getId()));
        } catch (Exception e) {
            return ApiResponse.fail(500, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询配置详情
     */
    @PostMapping("/detail")
    public ApiResponse<ShopConfigDO> getConfigDetail(@RequestBody ShopConfigRequest request) {
        if (request.getId() == null) {
            return ApiResponse.fail(400, "配置ID不能为空");
        }
        return ApiResponse.success(shopConfigService.getById(request.getId()));
    }
}
