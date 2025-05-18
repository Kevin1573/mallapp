package com.wx.service;

import com.wx.orm.entity.UserProfileDO;

public interface TokenService {

    UserProfileDO getUserByToken(String token);
}
