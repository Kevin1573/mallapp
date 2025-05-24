package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.common.exception.BizException;
import com.wx.orm.entity.UserProfileDO;
import com.wx.orm.mapper.UserProfileMapper;
import com.wx.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfileDO getUserByToken(String token) {
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getToken, token);
        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);
        if (Objects.isNull(userProfileDO)) {
            throw new BizException("token失效,请登录");
        }
        return userProfileDO;
    }
}
