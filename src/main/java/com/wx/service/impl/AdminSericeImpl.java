package com.wx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wx.common.enums.CompleteEnum;
import com.wx.common.exception.BizException;
import com.wx.common.model.request.*;
import com.wx.common.model.response.*;
import com.wx.common.utils.NumUtil;
import com.wx.orm.entity.*;
import com.wx.orm.mapper.*;
import com.wx.service.AdminService;
import com.wx.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AdminSericeImpl implements AdminService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsHistoryMapper goodsHistoryMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private RebateMapper rebateMapper;
    @Autowired
    private UserAddrMapper userAddrMapper;

    @Override
    public void initData() {
        List<GoodsHistoryDO> goodsHistoryDOS = goodsHistoryMapper.selectList(new LambdaQueryWrapper<>());
        for (GoodsHistoryDO goodsHistoryDO : goodsHistoryDOS) {
            UserAddrDO userAddrDO = userAddrMapper.selectById(goodsHistoryDO.getAddrId());
            if (Objects.isNull(userAddrDO)) {
                LambdaQueryWrapper<UserAddrDO> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserAddrDO::getUserId, goodsHistoryDO.getUserId());
                userAddrDO = userAddrMapper.selectList(queryWrapper).get(0);
            }
            if (Objects.nonNull(userAddrDO)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", userAddrDO.getName());
                jsonObject.put("phone", userAddrDO.getPhone());
                jsonObject.put("addr", userAddrDO.getProvince() + userAddrDO.getCity() + userAddrDO.getArea() + userAddrDO.getDetail());
                goodsHistoryDO.setOrderInfo(JSON.toJSONString(jsonObject));
                goodsHistoryMapper.updateById(goodsHistoryDO);
            }
        }
    }

    @Override
    public void uploadGoods(UploadGoodsRequest request) {
        int num = NumUtil.countDecimalPlaces(request.getPrice());
        if (num > 1) {
            throw new BizException("请输入正确的商品价格，价格保留一位小数");
        }

        GoodsDO goodsDO = new GoodsDO()
                .setGoodsPic(request.getGoodsPic())
                .setDescription(request.getDescription())
                .setName(request.getName())
                .setExt(request.getExtList())
                .setPrice(request.getPrice())
                .setCreateTime(new Date())
                .setModifyTime(new Date());
        goodsMapper.insert(goodsDO);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public QueryOrderHistoryResponse queryOrderHistory(QueryOrderHistoryRequest request) {
        // 根据用户信息是否为空，适配后台接口
        if (Objects.isNull(request.getToken())) {
            throw new BizException("请传入用户信息");
        }
        UserProfileDO user = tokenService.getUserByToken(request.getToken());
        // 查询订单历史信息(考虑到购物车组合下单，这里先分页查询出订单号列表，再去查询填充数据)
        QueryWrapper<GoodsHistoryDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT (trade_no), create_time");

        if (Objects.nonNull(request.getStatus())) {
            queryWrapper.eq("status", request.getStatus());
        } else {
            throw new BizException("请传入订单状态");
        }

//        if (Objects.nonNull(request.getNickname())) {
//            queryWrapper.apply("JSON_EXTRACT(order_info, '$.name') = {0}", request.getNickname());
//        }
        queryWrapper.orderByDesc("create_time");

        if (Objects.nonNull(user)) {
            queryWrapper.eq("user_id", user.getId());
        }
        Page<GoodsHistoryDO> page = new Page<>(request.getPage(), request.getLimit());
        IPage<GoodsHistoryDO> historyDOPage = goodsHistoryMapper.selectPage(page, queryWrapper);
        List<GoodsHistoryDO> records = historyDOPage.getRecords();
        long pages = historyDOPage.getPages();
        if (CollectionUtils.isEmpty(records)) {
            QueryOrderHistoryResponse queryOrderHistoryResponse = new QueryOrderHistoryResponse();
            queryOrderHistoryResponse.setRecords(new ArrayList<>());
            queryOrderHistoryResponse.setPage(request.getPage());
            queryOrderHistoryResponse.setTotal(0L);
            queryOrderHistoryResponse.setLimit(request.getLimit());
            return queryOrderHistoryResponse;
        }
        List<String> tradeNoList = new ArrayList<>();
        for (GoodsHistoryDO goodsHistoryDO : records) {
            tradeNoList.add(goodsHistoryDO.getTradeNo());
        }

        // 根据查询出来的订单号，封装数据(key:订单号， value:订单详情列表)
        List<QueryOrderHistoryModel> recordList = new ArrayList<>();
        for (String tradeNo : tradeNoList) {
            // 根据订单号查询对应的订单列表
            LambdaQueryWrapper<GoodsHistoryDO> tradeNoQuery = new LambdaQueryWrapper<>();
            tradeNoQuery.eq(GoodsHistoryDO::getTradeNo, tradeNo);
            List<GoodsHistoryDO> goodsHistoryDOS = goodsHistoryMapper.selectList(tradeNoQuery);
            List<QueryOrderGoodsModel> queryOrderGoodsModelList = new ArrayList<>();
            // 封装订单下的商品信息
            for (GoodsHistoryDO goodsHistoryDO : goodsHistoryDOS) {
                String goodsListJsonStr = goodsHistoryDO.getGoodsList();
                try {
                    String normalizedJson = goodsListJsonStr
                            .replace("\\\"", "\"")
                            .replace("\"[", "[")
                            .replace("]\"", "]");
                    List<QueryOrderGoodsModel> queryOrderGoodsModels = objectMapper.readValue(
                            normalizedJson,
                            new TypeReference<List<QueryOrderGoodsModel>>() {
                            }
                    );
                    queryOrderGoodsModelList.addAll(queryOrderGoodsModels);
                } catch (JsonProcessingException e) {
                    throw new BizException("订单商品数据解析失败: " + e.getMessage());
                }
            }
            double totalPrice = queryOrderGoodsModelList.stream()
                    .mapToDouble(goods -> goods.getPrice() * goods.getNum())
                    .sum();

            GoodsHistoryDO goodsHistoryDO1 = goodsHistoryDOS.get(0);
            JSONObject orderInfo = JSON.parseObject(goodsHistoryDO1.getOrderInfo());
            UserProfileDO userProfileDO = userProfileMapper.selectById(goodsHistoryDO1.getUserId());
            QueryOrderHistoryModel queryOrderHistoryModel = new QueryOrderHistoryModel();
            queryOrderHistoryModel.setOrderTime(goodsHistoryDO1.getCreateTime());
            queryOrderHistoryModel.setLogistics(goodsHistoryDO1.getLogistics());
            queryOrderHistoryModel.setLogisticsId(goodsHistoryDO1.getLogisticsId());
            queryOrderHistoryModel.setTradeNo(goodsHistoryDO1.getTradeNo());
            queryOrderHistoryModel.setGoodsModelList(queryOrderGoodsModelList);
            queryOrderHistoryModel.setIsPaySuccess(goodsHistoryDO1.getIsPaySuccess());
            queryOrderHistoryModel.setIsComplete(goodsHistoryDO1.getIsComplete());
            queryOrderHistoryModel.setTotalPrice(totalPrice);
            queryOrderHistoryModel.setRealName(userProfileDO.getNickName());
            queryOrderHistoryModel.setRealPhone(userProfileDO.getPhone());
            queryOrderHistoryModel.setAddr(orderInfo.getString("addr"));
            queryOrderHistoryModel.setPhone(orderInfo.getString("phone"));
            queryOrderHistoryModel.setUserName(orderInfo.getString("name"));
            queryOrderHistoryModel.setStatus(goodsHistoryDO1.getStatus());
            recordList.add(queryOrderHistoryModel);
        }

        QueryOrderHistoryResponse response = new QueryOrderHistoryResponse();
        response.setRecords(recordList);
        response.setPage(request.getPage() > pages ? pages : request.getPage());
        response.setTotal(historyDOPage.getTotal());
        response.setLimit(request.getLimit());
        return response;
    }

    @Override
    public void completeOrder(CompleteOrderRequest request) {
        LambdaQueryWrapper<GoodsHistoryDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoodsHistoryDO::getTradeNo, request.getTradeNo());
        GoodsHistoryDO goodsHistoryDO = new GoodsHistoryDO();
        goodsHistoryDO.setIsComplete(CompleteEnum.TRUE.getCode());
        goodsHistoryMapper.update(goodsHistoryDO, queryWrapper);
    }

    @Override
    public void updateGoodsHisLogistics(CompleteOrderRequest request) {
        LambdaQueryWrapper<GoodsHistoryDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoodsHistoryDO::getTradeNo, request.getTradeNo());
        GoodsHistoryDO goodsHistoryDO = new GoodsHistoryDO();
        goodsHistoryDO.setLogistics(request.getLogistics());
        goodsHistoryDO.setLogisticsId(request.getLogisticsId());
        goodsHistoryMapper.update(goodsHistoryDO, queryWrapper);
    }

    @Override
    public void deleteGoodById(DeleteCommunityRequest request) {
        goodsMapper.deleteById(request.getId());
    }

    @Override
    public void updateUserPosition(UpdateUserPositionRequest request) {
        // 根据用户信息是否为空，适配后台接口
        UserProfileDO user = userProfileMapper.selectById(request.getId());
        if (Objects.nonNull(request.getPosition())) {
//            user.setPosition(request.getPosition());
        }
//        if (Objects.nonNull(request.getPoint())) {
//            user.setPoint(request.getPoint());
//        }
//        if (Objects.nonNull(request.getRealPoint())) {
//            user.setRealPoint(request.getRealPoint());
//        }
        userProfileMapper.updateById(user);
    }

    @Override
    public void updateGoods(UpdateGoodsRequest request) {
        LambdaQueryWrapper<GoodsDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoodsDO::getId, request.getId());
        GoodsDO goodsDO = new GoodsDO();
        BeanUtils.copyProperties(request, goodsDO);
        goodsMapper.update(goodsDO, queryWrapper);
    }

    @Override
    public void addGoodsType(UploadGoodsRequest request) {
//        GoodsTypeDO goodsTypeDO = new GoodsTypeDO();
//        goodsTypeDO.setType(request.getType());
//        goodsTypeDO.setModifyTime(new Date());
//        goodsTypeDO.setCreateTime(new Date());
//        goodsTypeMapper.insert(goodsTypeDO);
    }

    @Override
    public List<GoodsTypeResponse> queryGoodsTypeList() {
//        LambdaQueryWrapper<GoodsTypeDO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.orderByAsc(GoodsTypeDO::getCreateTime);
//        List<GoodsTypeDO> goodsTypeDOS = goodsTypeMapper.selectList(queryWrapper);
        List<GoodsTypeResponse> typeList = new ArrayList<>();
//        for (GoodsTypeDO goodsTypeDO : goodsTypeDOS) {
//            GoodsTypeResponse response = new GoodsTypeResponse();
//            response.setType(goodsTypeDO.getType());
//            response.setTypeId(goodsTypeDO.getId());
//            typeList.add(response);
//        }
        return typeList;
    }

    @Override
    public void deleteTypeById(GoodsTypeResquest request) {
//        goodsTypeMapper.deleteById(request.getTypeId());
    }

    @Override
    public PageQueryMemberResponse queryUserList(PageQueryUserRequest request) {
        Page<UserProfileDO> page = new Page<>(request.getPage(), request.getLimit());
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        // 支持根据手机号查询单个用户
        queryWrapper.eq(null != request.getPhone(), UserProfileDO::getPhone, request.getPhone());
        queryWrapper.orderByDesc(UserProfileDO::getCreateTime);
        queryWrapper.isNotNull(UserProfileDO::getPhone);
        queryWrapper.isNotNull(UserProfileDO::getNickName);
        Page<UserProfileDO> userProfileDOPage = userProfileMapper.selectPage(page, queryWrapper);
        List<UserProfileDO> records = userProfileDOPage.getRecords();
        List<UserProfileModel> modelList = new ArrayList<>();
        for (UserProfileDO userProfileDO : records) {
            UserProfileModel model = new UserProfileModel();
            BeanUtils.copyProperties(userProfileDO, model);
            modelList.add(model);
        }

        PageQueryMemberResponse response = new PageQueryMemberResponse();
        response.setData(modelList);
        response.setLimit(request.getLimit());
        response.setPage(request.getPage());
        response.setTotal(userProfileDOPage.getTotal());
        return response;
    }

    @Override
    public void updateUserRebate(updateUserRebateRequest request) {
        LambdaQueryWrapper<RebateDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RebateDO::getPositionCode, request.getCode());
        RebateDO rebateDO = new RebateDO();
        rebateDO.setRatio(request.getRebate());
        rebateMapper.update(rebateDO, queryWrapper);
    }

    @Override
    public List<UserRebateResponse> queryUserRebateResponse() {
        LambdaQueryWrapper<RebateDO> queryWrapper = new LambdaQueryWrapper<>();
        List<RebateDO> rebateDOS = rebateMapper.selectList(queryWrapper);
        List<UserRebateResponse> rebateResponseList = new ArrayList<>();
        for (RebateDO rebateDO : rebateDOS) {
            UserRebateResponse response = new UserRebateResponse();
            response.setRebate(rebateDO.getRatio());
            response.setCode(rebateDO.getPositionCode());
            rebateResponseList.add(response);
        }
        return rebateResponseList;
    }

    @Override
    @Transactional
    public void updateUserPointNew(UserPointNewRequest request) {
        UserProfileDO userProfileDO = userProfileMapper.selectById(request.getUserId());
//        if (Objects.nonNull(request.getPoint())) {
//            userProfileDO.setPoint(userProfileDO.getPoint() + request.getPoint());
//        }
//        if (Objects.nonNull(request.getRealPoint())) {
//            userProfileDO.setRealPoint(userProfileDO.getRealPoint() + request.getRealPoint());
//        }
//        userProfileMapper.updateById(userProfileDO);

        // 记录充值流水
//        UserPointHisDO userPointHisDO = new UserPointHisDO();
//        userPointHisDO.setUserId(userProfileDO.getId());
//        userPointHisDO.setEditPoint(request.getPoint());
//        userPointHisDO.setEditRealPoint(request.getRealPoint());
//        userPointHisDO.setCreateTime(new Date());
//        userPointHisDO.setModifyTime(new Date());
//        userPointHisMapper.insert(userPointHisDO);
        log.info("update user point, point = {}, realPoint = {}, userId = {}", request.getPoint(), request.getRealPoint(), request.getUserId());
    }

    @Override
    public QueryMyPerformanceResponse queryMyPerformance(QueryMyPerformanceRequest request) {
        // 查询出对应的用户信息和商品信息
        UserProfileDO userProfile = tokenService.getUserByToken(request.getToken());

        if (Objects.isNull(userProfile)) {
            throw new BizException("user profile is error");
        }

        // 查询总业绩,先查询出下线人员(区分直接间接)
        QueryMyPerformanceResponse queryMyPerformanceResponse = new QueryMyPerformanceResponse();
        LambdaQueryWrapper<UserProfileDO> userProfileQuery = new LambdaQueryWrapper<>();
        userProfileQuery.eq(UserProfileDO::getId, userProfile.getId());
        List<UserProfileDO> userProfileDOS = userProfileMapper.selectList(userProfileQuery);
        if (CollectionUtils.isEmpty(userProfileDOS)) {
            return queryMyPerformanceResponse;
        }
        List<Long> userIdList = new ArrayList<>();
        for (UserProfileDO userDO : userProfileDOS) {
            userIdList.add(userDO.getId());
        }

        // 如果是间接业绩，则查询更下一级的人员
//        List<Long> newIdList = new ArrayList<>();
//        if (Objects.nonNull(request.getType()) && request.getType() == 2) {
//            LambdaQueryWrapper<UserProfileDO> userProfileQueryNext = new LambdaQueryWrapper<>();
//            userProfileQueryNext.in(UserProfileDO::getInviteUserId, userIdList);
//            List<UserProfileDO> userProfileDOS1 = userProfileMapper.selectList(userProfileQueryNext);
//            if (CollectionUtils.isEmpty(userProfileDOS1)) {
//                return queryMyPerformanceResponse;
//            }
//            for (UserProfileDO userProfileDO1 : userProfileDOS1) {
//                newIdList.add(userProfileDO1.getId());
//            }
//        }
//
//        // 根据查询出来的用户id列表，统计积分总数
//        QueryWrapper<UserPointHisDO> userPointQuery = new QueryWrapper<>();
//        userPointQuery.select("SUM(edit_point) as totalEditPoint, SUM(edit_real_point) as totalEditRealPoint," +
//                "SUM(used_point) as totalUsedPoint, SUM(used_real_point) as totalUsedRealPoint");
//        if (Objects.nonNull(request.getType()) && request.getType() == 1) {
//            userPointQuery.in("user_id", userIdList);
//        } else {
//            userPointQuery.in("user_id", newIdList);
//        }
//        userPointQuery.ge("create_time", new Date(request.getStartTimeStamp()));
//        userPointQuery.lt("create_time", new Date(request.getEndTimeStamp()));
//        List<Map<String, Object>> maps = userPointHisMapper.selectMaps(userPointQuery);
//        if (CollectionUtils.isEmpty(maps) || Objects.isNull(maps.get(0))) {
//            queryMyPerformanceResponse.setTotalUsedPoint(0L);
//            queryMyPerformanceResponse.setTotalUsedRealPoint(0L);
//            queryMyPerformanceResponse.setTotalRechargePoint(0L);
//            queryMyPerformanceResponse.setTotalRechargeRealPoint(0L);
//        } else {
//            queryMyPerformanceResponse.setTotalUsedPoint(((BigDecimal) maps.get(0).get("totalUsedPoint")).longValue());
//            queryMyPerformanceResponse.setTotalUsedRealPoint(((BigDecimal) maps.get(0).get("totalUsedRealPoint")).longValue());
//            queryMyPerformanceResponse.setTotalRechargePoint(((BigDecimal) maps.get(0).get("totalEditPoint")).longValue());
//            queryMyPerformanceResponse.setTotalRechargeRealPoint(((BigDecimal) maps.get(0).get("totalEditRealPoint")).longValue());
//        }
//
//        // 查询下线积分使用详情
//        Page<UserProfileDO> page = new Page<>(request.getPage(), request.getLimit());
//        LambdaQueryWrapper<UserProfileDO> newUserProfileQuery = new LambdaQueryWrapper<>();
//        if (Objects.nonNull(request.getType()) && request.getType() == 1) {
//            newUserProfileQuery.eq(UserProfileDO::getInviteUserId, userProfileDO.getId());
//        } else {
//            newUserProfileQuery.in(UserProfileDO::getInviteUserId, userIdList);
//        }
//        Page<UserProfileDO> userProfileDOPage = userProfileMapper.selectPage(page, newUserProfileQuery);
//        queryMyPerformanceResponse.setLimit(request.getLimit());
//        queryMyPerformanceResponse.setPage(request.getPage());
//        queryMyPerformanceResponse.setTotal(userProfileDOPage.getTotal());
//        List<UserProfileDO> records = userProfileDOPage.getRecords();
//
//        List<QueryMyPerformanceModel> modelList = new ArrayList<>();
//        for (UserProfileDO newUserProfileDO : records) {
//            QueryWrapper<UserPointHisDO> newUserPointQuery = new QueryWrapper<>();
//            newUserPointQuery.select("SUM(edit_point) as totalEditPoint, SUM(edit_real_point) as totalEditRealPoint," +
//                    "SUM(used_point) as totalUsedPoint, SUM(used_real_point) as totalUsedRealPoint");
//            newUserPointQuery.eq("user_id", newUserProfileDO.getId());
//            newUserPointQuery.ge("create_time", new Date(request.getStartTimeStamp()));
//            newUserPointQuery.lt("create_time", new Date(request.getEndTimeStamp()));
//            List<Map<String, Object>> resultMap = userPointHisMapper.selectMaps(newUserPointQuery);
//
//            QueryMyPerformanceModel model = new QueryMyPerformanceModel();
//            if (CollectionUtils.isEmpty(resultMap) || Objects.isNull(resultMap.get(0))) {
//                model.setUsedPoint(0L);
//                model.setUsedRealPoint(0L);
//                model.setRechargePoint(0L);
//                model.setRechargeRealPoint(0L);
//            } else {
//                model.setUsedPoint(((BigDecimal) resultMap.get(0).get("totalUsedPoint")).longValue());
//                model.setUsedRealPoint(((BigDecimal) resultMap.get(0).get("totalUsedRealPoint")).longValue());
//                model.setRechargePoint(((BigDecimal) resultMap.get(0).get("totalEditPoint")).longValue());
//                model.setRechargeRealPoint(((BigDecimal) resultMap.get(0).get("totalEditRealPoint")).longValue());
//            }
//            model.setHeadUrl(newUserProfileDO.getHeadUrl());
//            model.setNickName(newUserProfileDO.getNickName());
//            UserProfileDO inviteUser = userProfileMapper.selectById(newUserProfileDO.getInviteUserId());
//            model.setInviteUserHeadUrl(inviteUser.getHeadUrl());
//            model.setInviteUserName(inviteUser.getNickName());
//            modelList.add(model);
//        }
//        queryMyPerformanceResponse.setModelList(modelList);
        return queryMyPerformanceResponse;
    }

    @Override
    public QueryUserPointHisResponse queryMyPointHis(QueryMyPerformanceRequest request) {
        // 查询出对应的用户信息和商品信息
//        String openid = userTokenService.getOpenidByToken(request.getToken());
//        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(UserProfileDO::getOpenid, openid);
//        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);
//        if (Objects.isNull(userProfileDO)) {
//            throw new BizException("openid is error");
//        }
//
//        // 查询用户充值记录
//        Page<UserPointHisDO> page = new Page<>(request.getPage(), request.getLimit());
//        LambdaQueryWrapper<UserPointHisDO> queryWrapper1 = new LambdaQueryWrapper<>();
//        queryWrapper1.eq(UserPointHisDO::getUserId, userProfileDO.getId());
//        if (1 == request.getPointType()) {
//            queryWrapper1.gt(UserPointHisDO::getEditPoint, 0);
//        } else {
//            queryWrapper1.gt(UserPointHisDO::getEditRealPoint, 0);
//        }
//        queryWrapper1.orderByDesc(UserPointHisDO::getCreateTime);
//        queryWrapper1.ge(UserPointHisDO::getCreateTime, new Date(request.getStartTimeStamp()));
//        queryWrapper1.lt(UserPointHisDO::getCreateTime, new Date(request.getEndTimeStamp()));
//        Page<UserPointHisDO> userPointHisDOS = userPointHisMapper.selectPage(page, queryWrapper1);
//        List<UserPointHisDO> records = userPointHisDOS.getRecords();
//
//        List<QueryUserPointHisModel> modelList = new ArrayList<>();
//        for (UserPointHisDO userPointHisDO : records) {
//            QueryUserPointHisModel model = new QueryUserPointHisModel();
//            model.setRealPoint(userPointHisDO.getEditRealPoint());
//            model.setPoint(userPointHisDO.getEditPoint());
//            model.setCreateTime(userPointHisDO.getCreateTime());
//            modelList.add(model);
//        }
        QueryUserPointHisResponse response = new QueryUserPointHisResponse();
        response.setLimit(response.getLimit());
        response.setPage(response.getPage());
//        response.setTotal(userPointHisDOS.getTotal());
//        response.setUserPointHisModelslist(modelList);
        return response;
    }
}
