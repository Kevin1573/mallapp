package com.wx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wx.orm.entity.PaymentConfig;

import java.util.List;

public interface PaymentConfigService extends IService<PaymentConfig> {
    List<PaymentConfig> getActiveConfigs();
    PaymentConfig getActiveConfig(String mchId, String channel);
}