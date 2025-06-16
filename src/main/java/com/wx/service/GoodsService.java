package com.wx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wx.common.model.request.GoodsQueryRequest;
import com.wx.common.model.request.GoodsRequest;
import com.wx.common.model.request.GoodsSpecsRequest;
import com.wx.orm.entity.GoodsDO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GoodsService extends IService<GoodsDO> {
    
    /**
     * 复杂条件分页查询（带扩展参数）
     */
    Page<GoodsDO> complexPageSearch(GoodsRequest condition);

    /**
     * 带事务的商品上下架
     */
    boolean updateGoodsStatus(Long goodsId, Integer targetStatus);

    /**
     * 库存扣减（带乐观锁）
     */
    boolean reduceInventory(Long goodsId, Integer quantity);

    /**
     * 商品销量增加
     */
    boolean increaseSales(Long goodsId, Integer quantity);

    String uploadGoodsImage(MultipartFile file);

    List<GoodsDO> findGoodsBySpecs(String specs, String name);

    List<GoodsDO> findGoodsBySpecs(List<GoodsSpecsRequest> request);

    Page<GoodsDO> findGoodsByTime(GoodsQueryRequest request);

    Double totalGoodsByTime(GoodsQueryRequest request);
}