package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wx.common.model.request.OrderListRequest;
import com.wx.orm.entity.GoodsHistoryDO;
import com.wx.orm.entity.UserProfileDO;
import com.wx.orm.mapper.GoodsHistoryMapper;
import com.wx.orm.mapper.UserProfileMapper;
import com.wx.service.GoodsHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class GoodsHistoryServiceImpl extends ServiceImpl<GoodsHistoryMapper, GoodsHistoryDO>
        implements GoodsHistoryService {

    private final UserProfileMapper userProfileMapper;
    private final GoodsHistoryMapper goodsHistoryMapper;

    public GoodsHistoryServiceImpl(UserProfileMapper userProfileMapper, GoodsHistoryMapper goodsHistoryMapper) {
        this.userProfileMapper = userProfileMapper;
        this.goodsHistoryMapper = goodsHistoryMapper;
    }

    @Override
    public BigDecimal totalAmountGoodsByTime(OrderListRequest request, UserProfileDO userProfile) {
        QueryWrapper<GoodsHistoryDO> queryWrapper = new QueryWrapper<>();
        if (Objects.nonNull(request.getStatus())) {
            if (request.getStatus() == 13) {
                queryWrapper.eq("status", 6)
                        .or()
                        .eq("status", 7);
            } else {
                queryWrapper.eq("status", request.getStatus());
            }
        }
        if (!"root".equalsIgnoreCase(userProfile.getSource())) {
            queryWrapper.eq("from_mall", userProfile.getFromShopName());
        }

        String userPhone = request.getUserPhone();
        // 通过昵称查询用户
        if (StringUtils.isNotBlank(userPhone)) {
            UserProfileDO userDO = userProfileMapper.selectOne(new LambdaQueryWrapper<UserProfileDO>().eq(UserProfileDO::getNickName, userPhone));
            if (Objects.nonNull(userDO)) {
                queryWrapper.eq("user_id", userDO.getId());
            }
        }

        queryWrapper.like(StringUtils.isNotBlank(request.getTradeNo()), "trade_no", request.getTradeNo());

        queryWrapper.select("sum(pay_amount) as totalAmount");

        if (StringUtils.isNotBlank(request.getStartTime()) && StringUtils.isNotBlank(request.getEndTime())) {
            queryWrapper.between("create_time", request.getStartTime(), request.getEndTime());
        }
        try {
            List<Map<String, Object>> mapList = goodsHistoryMapper.selectMaps(queryWrapper);
            Map<String, Object> result = mapList == null ? new HashMap<>() : mapList.get(0);
            BigDecimal total = (BigDecimal) result.get("totalAmount");
            return total == null ? BigDecimal.ZERO : total;
        } catch (Exception e) {
            log.error(" goodsHistoryMapper.selectMaps", e);
        }
        return BigDecimal.ZERO;
    }
}
