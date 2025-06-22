package com.wx.miniapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wx.miniapp.dto.WxUserInfo;
import com.wx.miniapp.entity.User;
import com.wx.miniapp.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    /**
     * 创建或更新用户信息
     * @param openid 用户openid
     * @param userInfo 微信用户信息
     * @return 用户ID
     */
    public Long createOrUpdateUser(String openid, WxUserInfo userInfo) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid, openid);
        User user = this.getOne(queryWrapper);

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
        }

        // 更新用户信息
        if (userInfo != null) {
            user.setNickname(userInfo.getNickName());
            user.setAvatarUrl(userInfo.getAvatarUrl());
            user.setUnionid(userInfo.getUnionId());
        }

        this.saveOrUpdate(user);
        return user.getId();
    }

    /**
     * 根据openid查询用户
     * @param openid 用户openid
     * @return 用户信息
     */
    public User getUserByOpenid(String openid) {
        return baseMapper.selectByOpenid(openid);
    }
}
