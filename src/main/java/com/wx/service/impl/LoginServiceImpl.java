package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.common.exception.BizException;
import com.wx.common.model.request.EditPasswordRequest;
import com.wx.common.model.request.LoginRequest;
import com.wx.common.model.request.TokenRequest;
import com.wx.common.model.request.UserProfileRequest;
import com.wx.common.model.response.LoginResponse;
import com.wx.orm.entity.RebateDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.orm.mapper.RebateMapper;
import com.wx.orm.mapper.UserProfileMapper;
import com.wx.service.LoginService;
import com.wx.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(qw -> qw
                .eq(UserProfileDO::getPhone, request.getPhone())
                .or()
                .eq(UserProfileDO::getNickName, request.getUserName())
        );
        //.eq(UserProfileDO::getPassword, request.getPassword()); // 保留密码必须匹配的条件

        List<UserProfileDO> userProfileDOS = userProfileMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userProfileDOS)) {
            throw new BizException("账号不存在");
        }

        // 3. BCrypt 密码验证（遍历匹配用户）
        UserProfileDO validUser = null;
        for (UserProfileDO user : userProfileDOS) {
            if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
                validUser = user;
                break;
            }
        }

        if (validUser == null) {
            throw new BizException("账号密码错误");
        }


//        List<UserProfileDO> userProfileDOS = userProfileMapper.selectList(queryWrapper);
//        if (CollectionUtils.isEmpty(userProfileDOS)) {
//            throw new BizException("账号密码错误");
//        }

        UserProfileDO userProfileDO1 = userProfileDOS.get(0);
//        if (!userProfileDO1.getFromShopName().equals(request.getFrom())) {
//            throw new BizException("账号密码错误");
//        }

        LoginResponse resonse = new LoginResponse();
        resonse.setHeadUrl(userProfileDO1.getHeadUrl());
        resonse.setUserId(userProfileDO1.getId());
        resonse.setToken(userProfileDO1.getToken());
        //resonse.setPosition(rebateMapper.selectById(userProfileDO1.getPosition()).getDescription());
        resonse.setPhone(userProfileDO1.getPhone());
        resonse.setNickName(userProfileDO1.getNickName());
        resonse.setFromMall(userProfileDO1.getFromShopName());
        resonse.setSource(userProfileDO1.getSource());
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
        userProfileDO.setFromShopName(request.getFrom());
        userProfileDO.setHeadUrl(request.getHeadUrl());
        userProfileDO.setToken(UUID.randomUUID().toString());
        userProfileDO.setPosition(0L);
        userProfileDO.setCreateTime(new Date());
        userProfileDO.setModifyTime(new Date());
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
    public LoginResponse getUserInfoByToken(TokenRequest request) {
        UserProfileDO userProfileDO = tokenService.getUserByToken(request.getToken());
        LoginResponse response = new LoginResponse();
        response.setNickName(userProfileDO.getNickName());
        response.setToken(userProfileDO.getToken());
        response.setPhone(userProfileDO.getPhone());
        response.setHeadUrl(userProfileDO.getHeadUrl());

        RebateDO rebateDO = rebateMapper.selectById(userProfileDO.getPosition());
        response.setPosition(Optional.ofNullable(rebateDO).map(RebateDO::getDescription).orElse(""));
        response.setUserId(userProfileDO.getId());
        response.setSource(userProfileDO.getSource());
        response.setFromMall(userProfileDO.getFromShopName());
        response.setSource(userProfileDO.getSource());
        return response;
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
