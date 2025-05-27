package com.wx.service;

import com.wx.orm.entity.RebateDO;

import java.util.List;

public interface RebateService {


    void initRebate(String fromMall);

    List<RebateDO> queryRebateList(String from);

    RebateDO queryRebateById(Long positionId);
}
