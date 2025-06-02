package com.wx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wx.orm.entity.ShopConfigDO;

public interface ShopConfigService extends IService<ShopConfigDO> {
    ShopConfigDO queryMerchantConfigByFrom(String from);
    // 可扩展自定义方法
}