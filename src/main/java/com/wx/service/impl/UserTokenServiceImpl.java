package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.orm.entity.UserTokenDO;
import com.wx.orm.mapper.UserTokenMapper;
import com.wx.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTokenServiceImpl implements UserTokenService {

    @Autowired
    private UserTokenMapper userTokenMapper;

    @Override
    public String getOpenidByToken(String token) {
        LambdaQueryWrapper<UserTokenDO> tokenQuery = new LambdaQueryWrapper<>();
        tokenQuery.eq(UserTokenDO::getToken, token);
        UserTokenDO userTokenDO = userTokenMapper.selectOne(tokenQuery);
        return userTokenDO.getOpenid();
    }
}
