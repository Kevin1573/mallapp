package com.wx.service.impl;

import com.wx.common.model.ShopConfigResponse;
import com.wx.common.model.request.ShopConfigRequest;
import com.wx.common.model.response.ShopConfigDOResponse;
import com.wx.orm.entity.RebateDO;
import com.wx.orm.entity.ShopConfigDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.orm.mapper.RebateMapper;
import com.wx.orm.mapper.ShopConfigMapper;
import com.wx.service.ShopService;
import com.wx.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService {
    private final ShopConfigMapper shopConfigMapper;
    private final TokenService tokenService;
    private final RebateMapper rebateMapper;

    @Override
    public ShopConfigResponse getShopConfigInfo(ShopConfigRequest request) {
        UserProfileDO userProfile = tokenService.getUserByToken(request.getToken());
        ShopConfigDO shopConfigDO = shopConfigMapper.selectByFrom(request.getFrom());
        RebateDO rebateDO = rebateMapper.selectById(userProfile.getPosition());
        return new ShopConfigResponse(shopConfigDO.getShopName(),
                shopConfigDO.getFreight(), rebateDO.getRatio());
    }

    @Override
    public ShopConfigDOResponse getShopInfo(ShopConfigRequest request) {
        String token = request.getToken();
        UserProfileDO userByToken = tokenService.getUserByToken(token);
        String source = userByToken.getSource();
        String fromShopName = userByToken.getFromShopName();
        ShopConfigDO shopConfigDO = shopConfigMapper.selectById(request.getId());
        ShopConfigDOResponse response = new ShopConfigDOResponse();
        BeanUtils.copyProperties(shopConfigDO, response);
        response.setSource(source);
        return response;
    }
}
