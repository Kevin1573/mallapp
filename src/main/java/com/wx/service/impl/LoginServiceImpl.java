package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.common.exception.BizException;
import com.wx.common.model.request.EditPasswordRequest;
import com.wx.common.model.request.LoginRequest;
import com.wx.common.model.request.TokenRequest;
import com.wx.common.model.request.UserProfileRequest;
import com.wx.common.model.response.LoginResonse;
import com.wx.orm.entity.UserProfileDO;
import com.wx.orm.mapper.GoodsHistoryMapper;
import com.wx.orm.mapper.RebateMapper;
import com.wx.orm.mapper.UserProfileMapper;
import com.wx.service.LoginService;
import com.wx.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private RebateMapper rebateMapper;
    @Autowired
    private TokenService tokenService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResonse login(LoginRequest request) {
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getNickName, request.getUserName());
        queryWrapper.eq(UserProfileDO::getPassword, request.getPassword());
        List<UserProfileDO> userProfileDOS = userProfileMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userProfileDOS)) {
            throw new BizException("账号密码错误");
        }

        UserProfileDO userProfileDO1 = userProfileDOS.get(0);
        if (!userProfileDO1.getFrom().equals(request.getFrom())) {
            throw new BizException("账号密码错误");
        }

        LoginResonse resonse = new LoginResonse();
        resonse.setHeadUrl(userProfileDO1.getHeadUrl());
        resonse.setUserId(userProfileDO1.getId());
        resonse.setToken(userProfileDO1.getToken());
        resonse.setPosition(rebateMapper.selectById(userProfileDO1.getPosition()).getDescription());
        resonse.setPhone(userProfileDO1.getPhone());
        resonse.setNickName(userProfileDO1.getNickName());
        return resonse;
    }

    @Override
    public void register(LoginRequest request) {
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getNickName, request.getUserName());
        List<UserProfileDO> userProfileDOS = userProfileMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(userProfileDOS)) {
            throw new BizException("账号已存在");
        }

        // 用户注册
        UserProfileDO userProfileDO = new UserProfileDO();
        userProfileDO.setPhone(request.getPhone());
        userProfileDO.setNickName(request.getUserName());
        userProfileDO.setPassword(request.getPassword());
        userProfileDO.setFrom(request.getFrom());
        userProfileDO.setHeadUrl(request.getHeadUrl());
        userProfileDO.setToken(UUID.randomUUID().toString());
        userProfileDO.setPosition(0);
        userProfileMapper.insert(userProfileDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserProfile(UserProfileRequest request) {
        UserProfileDO userProfileDO = tokenService.getUserByToken(request.getToken());
        userProfileDO.setHeadUrl(request.getHeadUrl());
        userProfileDO.setNickName(request.getNickName());
        userProfileDO.setPhone(request.getPhone());
        userProfileDO.setPosition(request.getPosition());
        userProfileDO.setModifyTime(new Date());
        userProfileMapper.updateById(userProfileDO);
    }

    @Override
    public LoginResonse getUserInfoByToken(TokenRequest request) {
        UserProfileDO userProfileDO = tokenService.getUserByToken(request.getToken());
        LoginResonse resonse = new LoginResonse();
        resonse.setNickName(userProfileDO.getNickName());
        resonse.setPhone(userProfileDO.getPhone());
        resonse.setHeadUrl(userProfileDO.getHeadUrl());
        resonse.setPosition(rebateMapper.selectById(userProfileDO.getPosition()).getDescription());
        resonse.setUserId(userProfileDO.getId());
        return resonse;
    }

    @Override
    public void editPassword(EditPasswordRequest request) {
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getNickName, request.getUserName());
        queryWrapper.eq(UserProfileDO::getPassword, request.getOldPassword());
        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);

        userProfileDO.setPassword(request.getNewPassword());
        userProfileMapper.updateById(userProfileDO);
    }


}
