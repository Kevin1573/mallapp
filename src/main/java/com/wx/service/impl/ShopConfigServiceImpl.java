package com.wx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wx.orm.entity.ShopConfigDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.orm.mapper.ShopConfigMapper;
import com.wx.service.ShopConfigService;
import com.wx.service.ShopService;
import com.wx.service.UserProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShopConfigServiceImpl
        extends ServiceImpl<ShopConfigMapper, ShopConfigDO>
        implements ShopConfigService {

    private final UserProfileService userProfileService;
    private final ShopService shopService;

    public ShopConfigServiceImpl(UserProfileService userProfileService, ShopService shopService) {
        this.userProfileService = userProfileService;
        this.shopService = shopService;
    }

    @Override
    public ShopConfigDO queryMerchantConfigByFrom(String from) {
        return this.baseMapper.selectByFrom(from);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createShopAndAccount(ShopConfigDO shopConfigDO) {

        boolean result = save(shopConfigDO);

        if (result) {
            // 通过电话号码查询昵称, 如果存在此电话号码, 则不允许创建用户
            List<UserProfileDO> userProfileDO = userProfileService.getUserByNickName(shopConfigDO.getContactPhone());
            if (!userProfileDO.isEmpty()) {
                throw new RuntimeException("该手机号码已存在");
            }

            // 初始化 商铺的用户等级折扣信息
            shopService.initShopUserLevelDiscount(shopConfigDO.getFromMall());
            // 初始化一个主店铺账户
            shopService.initShopAccount(shopConfigDO.getContactPhone(), shopConfigDO.getFromMall());
        }
        return result;
    }
}