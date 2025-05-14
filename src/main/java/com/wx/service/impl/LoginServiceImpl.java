package com.wx.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wx.common.enums.PositionEnum;
import com.wx.common.exception.BizException;
import com.wx.common.model.request.LoginRequest;
import com.wx.common.model.request.TokenRequest;
import com.wx.common.model.request.UserProfileRequest;
import com.wx.common.model.response.LoginResonse;
import com.wx.common.model.response.OrderGoodsResponse;
import com.wx.common.utils.Constants;
import com.wx.common.utils.HttpUtil;
import com.wx.orm.entity.*;
import com.wx.orm.mapper.*;
import com.wx.service.LoginService;
import com.wx.service.UserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private UserTokenMapper userTokenMapper;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private Config payConfig;
    @Autowired
    private GoodsHistoryMapper goodsHistoryMapper;
    @Autowired
    private AccessTokenMapper accessTokenMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResonse login(LoginRequest request) throws Exception {
        // 请求wx登陆接口，获取登陆信息
        Map<String, String> param = new HashMap<>();
        param.put("appid", Constants.APP_ID);
        param.put("secret", Constants.APP_SECRET);
        param.put("js_code", request.getLoginCode());
        param.put("grant_type", "authorization_code");
        String result = HttpUtil.get(Constants.WX_LOGIN_URL, param);
        log.info("login result = {}", result);
        JSONObject resultObj = JSONObject.parseObject(result);
        String openid = resultObj.getString("openid");

        // 存入token和openid对应表
        LambdaQueryWrapper<UserTokenDO> tokenQuery = new LambdaQueryWrapper<>();
        tokenQuery.eq(UserTokenDO::getOpenid, openid);
        UserTokenDO userTokenDO = userTokenMapper.selectOne(tokenQuery);
        String token = UUID.randomUUID().toString().replace("_", "");
        if (Objects.isNull(userTokenDO)) {
            UserTokenDO tokenDO = new UserTokenDO();
            tokenDO.setToken(token);
            tokenDO.setOpenid(openid);
            tokenDO.setCreateTime(new Date());
            tokenDO.setModifyTime(new Date());
            userTokenMapper.insert(tokenDO);
        } else {
            userTokenDO.setToken(token);
            userTokenMapper.updateById(userTokenDO);
        }

        // 根据openid查询用户信息
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getOpenid, openid);
        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);

        LoginResonse resonse = new LoginResonse();
        if (Objects.nonNull(userProfileDO)) {
            resonse.setNickName(userProfileDO.getNickName());
            resonse.setPhone(userProfileDO.getPhone());
            resonse.setHeadUrl(userProfileDO.getHeadUrl());
            resonse.setAddr(userProfileDO.getAddr());
        } else {
            UserProfileDO newUserProfileDO = new UserProfileDO();
            newUserProfileDO.setOpenid(openid);
            newUserProfileDO.setModifyTime(new Date());
            newUserProfileDO.setHeadUrl(Constants.DEFAULT_HEAD);
            newUserProfileDO.setCreateTime(new Date());
            newUserProfileDO.setPosition(PositionEnum.VIP_CUSTOMERS.getCode());
            userProfileMapper.insert(newUserProfileDO);
        }
        resonse.setToken(token);
        return resonse;
    }

    @Override
    public void register(LoginRequest request) throws Exception {
        String openid = userTokenService.getOpenidByToken(request.getToken());

        // 未注册的用户则先注册；根据code查询用户手机号
        AccessTokenDO accessTokenDO = accessTokenMapper.selectOne(new LambdaQueryWrapper<>());
        if (Objects.isNull(accessTokenDO)) {
            AccessTokenDO newAccessToken = new AccessTokenDO();
            newAccessToken.setAccessToken(getWxAccessToken());
            newAccessToken.setModifyTime(new Date());
            newAccessToken.setCreateTime(new Date());
            accessTokenMapper.insert(newAccessToken);
            accessTokenDO = newAccessToken;
        }

        String accessToken = accessTokenDO.getAccessToken();
        String phoneByCode = getPhoneByCode(request.getPhoneCode(), accessToken);
        if (Objects.isNull(phoneByCode)) {
            // accessToken过期，则刷新token
            String newAccessToken = getWxAccessToken();
            accessTokenDO.setAccessToken(newAccessToken).setModifyTime(new Date());
            accessTokenMapper.updateById(accessTokenDO);

            // 第一次获取phone失败，刷新code后再次获取
            phoneByCode = getPhoneByCode(request.getPhoneCode(), newAccessToken);
        }

        // 存入用户昵称/手机号
        UserProfileDO newUserProfileDO = new UserProfileDO();
        newUserProfileDO.setPhone(phoneByCode);
        newUserProfileDO.setNickName("用户" + phoneByCode);
        newUserProfileDO.setInviteUserId(request.getInviteUserId());
        newUserProfileDO.setPosition(PositionEnum.VIP_CUSTOMERS.getCode());
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getOpenid, openid);
        userProfileMapper.update(newUserProfileDO, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserProfile(UserProfileRequest request) {
        // 根据登陆token查询出对应的openid
        String openid = userTokenService.getOpenidByToken(request.getToken());
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getOpenid, openid);
        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);
        if (Objects.isNull(userProfileDO)) {
            UserProfileDO profileDO = new UserProfileDO()
                    .setAddr(request.getAddr())
                    .setHeadUrl(request.getHeadUrl())
                    .setOpenid(openid)
                    .setPhone(request.getPhone())
                    .setNickName(request.getNickName())
                    .setCreateTime(new Date())
                    .setModifyTime(new Date());
            userProfileMapper.insert(profileDO);
        } else {
            userProfileDO.setAddr(request.getAddr());
            userProfileDO.setHeadUrl(request.getHeadUrl());
            userProfileDO.setNickName(request.getNickName());
            userProfileDO.setPhone(request.getPhone());
            userProfileDO.setModifyTime(new Date());
            userProfileMapper.updateById(userProfileDO);
        }
    }

    @Override
    public LoginResonse getUserInfoByToken(TokenRequest request) {
        String openid = userTokenService.getOpenidByToken(request.getToken());
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getOpenid, openid);
        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);
        LoginResonse resonse = new LoginResonse();
        resonse.setNickName(userProfileDO.getNickName());
        resonse.setPhone(userProfileDO.getPhone());
        resonse.setHeadUrl(userProfileDO.getHeadUrl());
        resonse.setAddr(userProfileDO.getAddr());
        resonse.setPosition(userProfileDO.getPosition());
        resonse.setUserId(userProfileDO.getId());
        resonse.setPoint(userProfileDO.getPoint());
        resonse.setRealPoint(userProfileDO.getRealPoint());
        return resonse;
    }

    // 获取微信调用凭证
    private String getWxAccessToken() throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("grant_type", "client_credential");
        param.put("appid", Constants.APP_ID);
        param.put("secret", Constants.APP_SECRET);
        String result = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token", param);
        JSONObject jsonObject = JSON.parseObject(result);
        return jsonObject.getString("access_token");
    }

    // 根据code获取手机号
    private String getPhoneByCode(String code, String accessToken) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        String result = HttpUtil.post("https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken, param);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONObject phoneInfo = jsonObject.getJSONObject("phone_info");
        if (Objects.isNull(phoneInfo)) {
            return null;
        }
        return phoneInfo.getString("purePhoneNumber");
    }

}
