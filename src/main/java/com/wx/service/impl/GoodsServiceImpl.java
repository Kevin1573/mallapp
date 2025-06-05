package com.wx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wx.common.exception.BizException;
import com.wx.common.model.request.GoodsRequest;
import com.wx.common.model.request.GoodsSpecsRequest;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.mapper.GoodsMapper;
import com.wx.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, GoodsDO> implements GoodsService {

    @Override
    public Page<GoodsDO> complexPageSearch(GoodsRequest condition) {
        return lambdaQuery()
            .like(StringUtils.isNotBlank(condition.getKeyword()), GoodsDO::getName, condition.getKeyword())
            .or()
            .like(StringUtils.isNotBlank(condition.getKeyword()), GoodsDO::getDescription, condition.getKeyword())
            .eq(condition.getCategory() != null, GoodsDO::getCategory, condition.getCategory())
            .eq(condition.getStatus() != null, GoodsDO::getStatus, condition.getStatus())
            .ge(condition.getMinPrice() != null, GoodsDO::getPrice, condition.getMinPrice())
            .le(condition.getMaxPrice() != null, GoodsDO::getPrice, condition.getMaxPrice())
            .page(condition.toPage());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateGoodsStatus(Long goodsId, Integer targetStatus) {
        GoodsDO goods = getById(goodsId);
        if (goods == null) {
            throw new BizException("商品不存在");
        }
        
        // 上下架校验逻辑
        if (targetStatus == 1 && goods.getInventory() <= 0) {
            throw new BizException("库存不足无法上架");
        }
        
        return lambdaUpdate()
            .set(GoodsDO::getStatus, targetStatus)
            .eq(GoodsDO::getId, goodsId)
            .update();
    }

    @Override
    public boolean reduceInventory(Long goodsId, Integer quantity) {
        return baseMapper.reduceInventory(goodsId, quantity) > 0;
    }

    @Override
    public boolean increaseSales(Long goodsId, Integer quantity) {
        return lambdaUpdate()
            .setSql("sales = sales + " + quantity)
            .eq(GoodsDO::getId, goodsId)
            .update();
    }

    // 实现示例
    @Override
    public String uploadGoodsImage(MultipartFile file) {
//        try {
//            // 实现文件上传逻辑（OSS/MinIO等）
//            String fileUrl = ossClient.upload(file);
//            return fileUrl;
//        } catch (IOException e) {
//            throw new BizException("文件上传失败");
//        }

        return "https://example.com/uploads/" + file.getOriginalFilename();
    }

    @Override
    public List<GoodsDO> findGoodsBySpecs(String specs, String name) {
        return new ArrayList<>(lambdaQuery()
                .eq(StringUtils.isNotBlank(name), GoodsDO::getName, name)
                .eq(StringUtils.isNotBlank(specs), GoodsDO::getSpecifications, specs)
                .list());

    }

    @Override
    public List<GoodsDO> findGoodsBySpecs(List<GoodsSpecsRequest> request) {
        // 实现根据规格查询商品逻辑
        if (request == null || request.isEmpty()) {
            return Collections.emptyList();
        }
        List<GoodsDO> list = new ArrayList<>();
        for (GoodsSpecsRequest specsRequest : request) {
            List<GoodsDO> oneGoods = lambdaQuery()
                    .eq(StringUtils.isNotBlank(specsRequest.getName()), GoodsDO::getName, specsRequest.getName())
                    .eq(StringUtils.isNotBlank(specsRequest.getSpecs()), GoodsDO::getSpecifications, specsRequest.getSpecs())
                    .list();
            if (oneGoods != null && !oneGoods.isEmpty()) {
                list.add(oneGoods.get(0));
            }
        }

        return list;
    }
}