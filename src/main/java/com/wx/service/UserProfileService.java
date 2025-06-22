package com.wx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wx.common.model.request.UserProfileRequest;
import com.wx.miniapp.dto.WxUserInfo;
import com.wx.orm.entity.UserProfileDO;

import java.util.List;

public interface UserProfileService extends IService<UserProfileDO> {
    UserProfileDO getUserBy(UserProfileRequest request);

    List<UserProfileDO> getUserByNickName( String contactPhone);

    UserProfileDO getUserByOpenId(String openid);

    Long createOrUpdateUser(String openid, String token, WxUserInfo userInfo);

    UserProfileDO getUserByToken(String token);
}