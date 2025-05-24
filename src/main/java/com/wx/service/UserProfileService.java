package com.wx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wx.common.model.request.UserProfileRequest;
import com.wx.orm.entity.UserProfileDO;

public interface UserProfileService extends IService<UserProfileDO> {
    UserProfileDO getUserBy(UserProfileRequest request);
}