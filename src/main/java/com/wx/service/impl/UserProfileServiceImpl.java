package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wx.common.model.request.UserProfileRequest;
import com.wx.orm.entity.UserProfileDO;
import com.wx.orm.mapper.UserProfileMapper;
import com.wx.service.TokenService;
import com.wx.service.UserProfileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfileDO>
        implements UserProfileService {
    private final TokenService tokenService;

    public UserProfileServiceImpl(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public UserProfileDO getUserBy(UserProfileRequest request) {
        QueryWrapper<UserProfileDO> wrapper = new QueryWrapper<>();

        // 按 ID 查询（最高优先级）
        if (request.getId() != null) {
            wrapper.eq("id", request.getId());
        }
        // 按手机号查询
        else if (StringUtils.isNotBlank(request.getPhone())) {
            wrapper.eq("phone", request.getPhone());
        }
        // 按 token 查询（需解析 token 获取用户 ID）
        else if (StringUtils.isNotBlank(request.getToken())) {
            UserProfileDO userByToken = tokenService.getUserByToken(request.getToken());
            if (userByToken == null) {
                throw new RuntimeException("无效的登录凭证");
            }

            wrapper.eq("id", userByToken.getId());
            return userByToken;
        }
        // 无有效查询条件
        else {
            throw new RuntimeException("缺少必要的查询参数");
        }

        UserProfileDO user = getOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    // 在保存前加密密码（添加在 service 层）
    public boolean save(UserProfileDO userProfile) {
        // 根据phone和nickName 判断是否存在
        QueryWrapper<UserProfileDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", userProfile.getPhone());
        queryWrapper.eq("nick_name", userProfile.getNickName());
        if (getOne(queryWrapper) != null) {
            throw new RuntimeException("用户已存在");
        }

        if (StringUtils.isNotBlank(userProfile.getPassword())) {
            String encryptedPwd = BCrypt.hashpw(userProfile.getPassword(), BCrypt.gensalt());
            userProfile.setPassword(encryptedPwd);
        } else {
            // 默认密码
            userProfile.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
        }
        return super.save(userProfile);
    }

    @Override
    public boolean updateById(UserProfileDO entity) {
        // 保留原始密码逻辑（当传入空密码时）
        if (StringUtils.isBlank(entity.getPassword())) {
            UserProfileDO original = getById(entity.getId());
            entity.setPassword(original.getPassword());
        }
        return super.updateById(entity);
    }

}