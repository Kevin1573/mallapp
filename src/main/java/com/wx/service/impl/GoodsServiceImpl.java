package com.wx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wx.common.exception.BizException;
import com.wx.common.model.request.GoodsRequest;
import com.wx.orm.entity.GoodsDO;
import com.wx.orm.mapper.GoodsMapper;
import com.wx.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
}