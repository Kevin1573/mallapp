package com.wx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wx.orm.entity.ShopConfigDO;
import com.wx.orm.mapper.ShopConfigMapper;
import com.wx.service.ShopConfigService;
import org.springframework.stereotype.Service;

@Service
public class ShopConfigServiceImpl 
     extends ServiceImpl<ShopConfigMapper, ShopConfigDO>
     implements ShopConfigService {

    @Override
    public ShopConfigDO queryMerchantConfigByFrom(String from) {
        return this.baseMapper.selectByFrom(from);
    }
}