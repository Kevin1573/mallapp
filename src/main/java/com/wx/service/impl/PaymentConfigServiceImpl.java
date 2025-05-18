package com.wx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wx.orm.entity.PaymentConfig;
import com.wx.orm.mapper.PaymentConfigMapper;
import com.wx.service.PaymentConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentConfigServiceImpl extends ServiceImpl<PaymentConfigMapper, PaymentConfig>
        implements PaymentConfigService {

    @Override
    public PaymentConfig getActiveConfig(String mchId, String channel) {
        return baseMapper.findActiveConfig(mchId, channel);
    }

    @Override
    public List<PaymentConfig> getActiveConfigs() {
        return baseMapper.selectByStatus(PaymentConfig.Status.ENABLED.getCode());
    }

}