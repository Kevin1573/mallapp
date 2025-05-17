package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.common.exception.BizException;
import com.wx.orm.entity.UserProfileDO;
import com.wx.orm.mapper.UserProfileMapper;
import com.wx.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class TokenServiceImpl implements TokenService {

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    public UserProfileDO getUserByToken(String token) {
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getToken, token);
        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);
        if (Objects.isNull(userProfileDO)) {
            throw new BizException("请登录");
        }
        return userProfileDO;
    }
}
