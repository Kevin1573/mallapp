package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wx.orm.entity.RebateDO;
import com.wx.orm.mapper.RebateMapper;
import com.wx.service.RebateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RebateServiceImpl implements RebateService {
    private final RebateMapper rebateMapper;

    @Override
    public void initRebate(String fromMall) {
        RebateDO rebateDO = new RebateDO();
        rebateDO.setRatio(1.00);
        rebateDO.setPositionCode("L1");
        rebateDO.setDescription("一级折扣比例");
        rebateDO.setCreateTime(new java.util.Date());
        rebateDO.setFromMall(fromMall);
        rebateMapper.insert(rebateDO);
        rebateDO = new RebateDO();
        rebateDO.setRatio(0.90);
        rebateDO.setPositionCode("L2");
        rebateDO.setDescription("二级折扣比例");
        rebateDO.setFromMall(fromMall);
        rebateDO.setCreateTime(new java.util.Date());
        rebateMapper.insert(rebateDO);
        rebateDO = new RebateDO();
        rebateDO.setRatio(0.80);
        rebateDO.setPositionCode("L3");
        rebateDO.setDescription("三级折扣比例");
        rebateDO.setFromMall(fromMall);
        rebateDO.setCreateTime(new java.util.Date());
        rebateMapper.insert(rebateDO);
    }

    @Override
    public List<RebateDO> queryRebateList(String from) {
        // 根据fromMall 查询店铺的折扣列表
        List<RebateDO> rebateList = rebateMapper.selectList(new QueryWrapper<RebateDO>().eq("from_mall", from));
        return rebateList == null ? Collections.emptyList() : rebateList;
    }

    @Override
    public RebateDO queryRebateById(Long positionId) {
        return rebateMapper.selectById(positionId);
    }
}
