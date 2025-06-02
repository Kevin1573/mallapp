package com.wx.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wx.common.enums.CompleteEnum;
import com.wx.common.enums.OrderStatus;
import com.wx.common.enums.PayWayEnums;
import com.wx.common.exception.BizException;
import com.wx.common.model.OrderListItem;
import com.wx.common.model.request.*;
import com.wx.common.model.response.*;
import com.wx.common.utils.LogisticsUtil;
import com.wx.common.utils.OrderUtil;
import com.wx.common.utils.WxAPIV3AesUtil;
import com.wx.orm.entity.*;
import com.wx.orm.mapper.*;
import com.wx.service.OrderService;
import com.wx.service.ShopService;
import com.wx.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private RebateMapper rebateMapper;
    @Autowired
    private GoodsHistoryMapper goodsHistoryMapper;
    @Autowired
    private ShoppingCarMapper shoppingCarMapper;
    @Autowired
    private UserAddrMapper userAddrMapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ShopService shopService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderGoodsResponse orderGoods(OrderGoodsRequest request) {
        UserProfileDO userByToken = tokenService.getUserByToken(request.getToken());
        if (Objects.isNull(userByToken)) {
            throw new BizException("请先登录");
        }

        // 查询出对应的用户信息和商品信息
//        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(UserProfileDO::getId, userByToken.getId());
//        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);
//        if (Objects.isNull(userProfileDO)) {
//            throw new BizException("token is error");
//        }

        // 检查商品库存
        for (OrderGoodsModelRequest modelRequest : request.getModelRequestList()) {
            GoodsDO goodsDO = goodsMapper.selectById(modelRequest.getGoodsId());
            if (Objects.isNull(goodsDO)) {
                throw new BizException("商品不存在");
            }
            Long num = modelRequest.getNum();
            if (num > goodsDO.getInventory()) {
                throw new BizException("商品" + goodsDO.getName() + " 库存不足");
            }
        }

        String tradeNo = OrderUtil.snowflakeOrderNo();
        // 1. 预收集所有商品信息
        List<QueryOrderGoodsModel> goodsList = new ArrayList<>();
        // 关键-初次创建的商品
        GoodsDO firstCreateGoods = null;
        for (OrderGoodsModelRequest modelRequest : request.getModelRequestList()) {
            GoodsDO goodsDO = goodsMapper.selectById(modelRequest.getGoodsId());
            Long num = modelRequest.getNum();
            if (goodsDO.getFirstGoods()) {
                firstCreateGoods = goodsDO;
            }
            QueryOrderGoodsModel queryOrderGoodsModel = new QueryOrderGoodsModel();
            queryOrderGoodsModel.setId(goodsDO.getId());
            queryOrderGoodsModel.setBrand(goodsDO.getBrand());
            queryOrderGoodsModel.setCategory(goodsDO.getCategory());
            queryOrderGoodsModel.setDescription(goodsDO.getDescription());
            queryOrderGoodsModel.setFirstGoods(goodsDO.getFirstGoods());
            queryOrderGoodsModel.setGoodsPic(goodsDO.getGoodsPic());
            queryOrderGoodsModel.setName(goodsDO.getName());
            queryOrderGoodsModel.setPrice(BigDecimal.valueOf(goodsDO.getPrice()));
            queryOrderGoodsModel.setSales(goodsDO.getSales());
            queryOrderGoodsModel.setNum(num);

            goodsList.add(queryOrderGoodsModel);
        }
        String goodsListJson = JSON.toJSONString(goodsList); // 转为JSON数组

        // 2. 添加购买记录（更新goodsList字段）
        UserAddrDO userAddrDO = userAddrMapper.selectById(request.getAddrId());
        JSONObject orderInfo = new JSONObject();
        if (Objects.nonNull(userAddrDO)) {
            orderInfo.put("name", userAddrDO.getName());
            orderInfo.put("phone", userAddrDO.getPhone());
            orderInfo.put("addr", userAddrDO.getProvince() + userAddrDO.getCity() + userAddrDO.getArea() + userAddrDO.getDetail());
        }

        // 计算商品的实际支付金额
        BigDecimal payAmount = BigDecimal.ZERO;
        for (QueryOrderGoodsModel queryOrderGoodsModel : goodsList) {
            payAmount = payAmount.add(queryOrderGoodsModel.getPrice().multiply(BigDecimal.valueOf(queryOrderGoodsModel.getNum())));
        }

        // 获取用户的折扣率
        Long position = userByToken.getPosition();
        RebateDO rebateDO = rebateMapper.selectById(position);

        // 获取店铺的运费
        BigDecimal freight = shopService.getFreight(request.getFrom());
        BigDecimal payAmount2;
        if (rebateDO == null || Objects.isNull(rebateDO.getRatio())) {
            payAmount2 = payAmount.add(freight).setScale(2, RoundingMode.DOWN);
        } else {
            payAmount2 = payAmount.multiply(BigDecimal.valueOf(rebateDO.getRatio())).add(freight).setScale(2, RoundingMode.DOWN);
        }
        GoodsHistoryDO goodsHistoryDO = new GoodsHistoryDO()
                .setUserId(userByToken.getId())
                .setIsComplete(CompleteEnum.FALSE.getCode())
                .setCreateTime(new Date())
                .setModifyTime(new Date())
                .setTradeNo(tradeNo)
                .setPayAmount(payAmount2)
                .setIsPaySuccess(CompleteEnum.FALSE.getCode())
                .setAddrId(request.getAddrId())
                .setOrderInfo(JSON.toJSONString(orderInfo))
                .setGoodsList(JSON.toJSONString(goodsListJson))
                .setFromMall(request.getFrom());

        LambdaQueryWrapper<GoodsHistoryDO> newQuery = new LambdaQueryWrapper<>();
        newQuery.eq(GoodsHistoryDO::getTradeNo, tradeNo);
        List<GoodsHistoryDO> goodsHistoryDOList = goodsHistoryMapper.selectList(newQuery);
        if (CollectionUtils.isNotEmpty(goodsHistoryDOList)) {
            goodsHistoryDO.setCreateTime(goodsHistoryDOList.get(0).getCreateTime());
            goodsHistoryDO.setModifyTime(goodsHistoryDOList.get(0).getModifyTime());
        }
        goodsHistoryMapper.insert(goodsHistoryDO);

        // 减少库存
        for (OrderGoodsModelRequest modelRequest : request.getModelRequestList()) {
            GoodsDO goodsDO = goodsMapper.selectById(modelRequest.getGoodsId());
            Long num = modelRequest.getNum();
            goodsDO.setInventory(goodsDO.getInventory() - num);
            goodsMapper.updateById(goodsDO);
        }


        // 邮寄方式改为邮寄和自提，自提则没有快递费
//        Double logisticsPrice = request.getFreight(); // 运费
//        if (Objects.isNull(logisticsPrice)) {
//            throw new BizException("请输入正确的地址信息");
//        }
//        if (Objects.nonNull(request.getAddrId())) {
        //UserAddrDO userAddrDO = userAddrMapper.selectById(request.getAddrId());
        //logisticsPrice = LogisticsUtil.getLogisticsPrice((int) Math.ceil(weight), userAddrDO.getProvince());
//        }

        OrderGoodsResponse orderGoodsResponse = new OrderGoodsResponse();
        orderGoodsResponse.setOutTradeNo(tradeNo);

        // TODO 验证优惠券的优惠金额和有效性
//        Long couponId = request.getCouponId();
//        if (Objects.nonNull(couponId)) {
//            RebateDO rebateDO = rebateMapper.selectById(couponId);
//            if (Objects.isNull(rebateDO)) {
//                throw new BizException("优惠券不存在");
//            }
//
//            orderGoodsResponse.setRebate(rebateDO.getRatio());
//            // 检查优惠券是否过期
//            if (rebateDO.getExpireTime().getTime() < System.currentTimeMillis()) {
//                throw new BizException("优惠券已过期");
//            }
//        }


//        Long nowPoint = userProfileDO.getPoint();
//        Long nowRealPoint = userProfileDO.getRealPoint();
//        if (Objects.isNull(nowPoint) || point > nowPoint) {
//            throw new BizException("积分不足");
//        }
//        if (Objects.isNull(nowRealPoint) || realPoint > nowRealPoint) {
//            throw new BizException("辅销品及快递费积分不足");
//        }

        // 记录用户使用积分总数
//        long remainPoint = nowPoint - point;
//        long remainRealPoint = nowRealPoint - realPoint;
//        userProfileDO.setPoint(remainPoint);
//        userProfileDO.setRealPoint(remainRealPoint);
//        userProfileDO.setCostPoint(userProfileDO.getCostPoint() + point);
//        userProfileDO.setCostRealPoint(userProfileDO.getCostRealPoint() + realPoint);
//        userProfileMapper.updateById(userProfileDO);

        // 记录订单使用积分、其他积分
        LambdaQueryWrapper<GoodsHistoryDO> hisQueryWrapper = new LambdaQueryWrapper<>();
        hisQueryWrapper.eq(GoodsHistoryDO::getTradeNo, tradeNo);
//        GoodsHistoryDO goodsHistoryDO = new GoodsHistoryDO();
//        goodsHistoryDO.setPoint(point);
//        goodsHistoryDO.setRealPoint(realPoint);
//        goodsHistoryMapper.update(goodsHistoryDO, hisQueryWrapper);

        // 记录使用流水
//        UserPointHisDO userPointHisDO = new UserPointHisDO();
//        userPointHisDO.setUserId(userProfileDO.getId());
//        userPointHisDO.setUsedPoint((int) point);
//        userPointHisDO.setUsedRealPoint((int)realPoint);
//        userPointHisDO.setCreateTime(new Date());
//        userPointHisDO.setModifyTime(new Date());
//        userPointHisMapper.insert(userPointHisDO);

        return orderGoodsResponse;
    }

    @Override
    public QueryGoodsResponse queryGoods(PageQueryGoodsRequest request) {
        // 分页查询数据
        Page<GoodsDO> page = new Page<>(request.getPage(), request.getLimit());
        LambdaQueryWrapper<GoodsDO> queryWrapper = new LambdaQueryWrapper<>();
        // 0, 商品名称
        queryWrapper.like(StringUtils.isNotBlank(request.getGoodsName()), GoodsDO::getName, request.getGoodsName());

        // 1. 分类筛选
        queryWrapper.eq(StringUtils.isNotBlank(request.getCategory()), GoodsDO::getCategory, request.getCategory());

        // 2. 品牌筛选（假设有 brand 字段）
        queryWrapper.eq(StringUtils.isNotBlank(request.getBrand()), GoodsDO::getBrand, request.getBrand());

        // 3. 预算区间筛选（新增逻辑）
        if (StringUtils.isNotBlank(request.getBudget())) {
            String[] budgetArr = request.getBudget().split(",");
            if (budgetArr.length == 2) {
                Double minBudget = Double.parseDouble(budgetArr[0]);
                Double maxBudget = Double.parseDouble(budgetArr[1]);
                queryWrapper.between(GoodsDO::getPrice, minBudget, maxBudget);
            }
        }

        // 4. 排序逻辑  销量，1升序2降序
        queryWrapper.orderBy(true, 1 == request.getInventory(), GoodsDO::getInventory);
        queryWrapper.orderByAsc(GoodsDO::getId);

        // 5. 查询主规格商品
        queryWrapper.eq(GoodsDO::getFirstGoods, true);
        queryWrapper.eq(GoodsDO::getFromMall, request.getFrom());

        IPage<GoodsDO> goodsDOPage = goodsMapper.selectPage(page, queryWrapper);

        List<GoodsDO> goodsDOList = goodsDOPage.getRecords();

        List<QueryGoodsModel> modelList = new ArrayList<>();
        for (GoodsDO goodsDO : goodsDOList) {
            QueryGoodsModel model = new QueryGoodsModel();
            model.setId(goodsDO.getId());
            model.setDescription(goodsDO.getDescription());
            model.setPrice(goodsDO.getPrice());
            model.setName(goodsDO.getName());
            model.setExt(goodsDO.getExt());
            model.setGoodsUnit(goodsDO.getGoodsUnit());
            model.setFirstGoods(goodsDO.getFirstGoods());
            model.setRecommendGoods(goodsDO.getRecommendGoods());

            model.setGoodsPic(goodsDO.getGoodsPic());
            model.setBrandPic(goodsDO.getBrandPic());


            modelList.add(model);
        }

        QueryGoodsResponse response = new QueryGoodsResponse();
        response.setRecords(modelList);
        response.setTotal(goodsDOPage.getTotal());
        response.setLimit(request.getLimit());
        response.setPage(request.getPage());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void wxPayCallback(JSONObject jsonObject) throws Exception {
        log.info("Wx pay callback, request = {}", JSONObject.toJSONString(jsonObject));
        // 支付状态校验
        String summary = jsonObject.getString("summary");
        if (!"支付成功".equals(summary)) {
            return;
        }

        // 解析支付出详情
        JSONObject resourceObj = jsonObject.getJSONObject("resource");
        String ciphertext = resourceObj.getString("ciphertext");
        String associatedData = resourceObj.getString("associated_data");
        String nonce = resourceObj.getString("nonce");
        String ciphertextDec = WxAPIV3AesUtil.decryptToString(associatedData.getBytes(), nonce.getBytes(), ciphertext);
        JSONObject textObj = JSONObject.parseObject(ciphertextDec);
        log.info("pay success, callback = {}", ciphertextDec);
        String outTradeNo = textObj.getString("out_trade_no");
        Long amount = textObj.getJSONObject("amount").getLong("total");

        // 更新支付状态（注意幂等性）
        LambdaQueryWrapper<GoodsHistoryDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoodsHistoryDO::getTradeNo, outTradeNo);
        List<GoodsHistoryDO> goodsHistoryDOList = goodsHistoryMapper.selectList(queryWrapper);
        for (GoodsHistoryDO historyDO : goodsHistoryDOList) {
            historyDO.setIsPaySuccess(CompleteEnum.TRUE.getCode());
            goodsHistoryMapper.updateById(historyDO);
        }

        // 用户上级代理返利,并扣除对应积分
        Long userId = goodsHistoryDOList.get(0).getUserId();
        UserProfileDO userProfileDO = userProfileMapper.selectById(userId);
//        Long inviteUserId = userProfileDO.getInviteUserId();
//        UserProfileDO inviteUser = userProfileMapper.selectById(inviteUserId);
//        Long remainPoint = inviteUser.getPoint() - amount / 10;
//        inviteUser.setPoint(remainPoint);
//        userProfileMapper.updateById(inviteUser);

        // 发起商家转账
//        InitiateBatchTransferRequest transferRequest = new InitiateBatchTransferRequest();
//        transferRequest.setAppid(Constants.APP_ID);
//        transferRequest.setOutBatchNo(outTradeNo);
//        transferRequest.setBatchName("顾客购物返点");
//        transferRequest.setBatchRemark("顾客购物返点");
//        transferRequest.setTotalAmount(amount);
//        transferRequest.setTotalNum(1);
//        TransferDetailInput detailInput = new TransferDetailInput();
//        detailInput.setOpenid(inviteUser.getOpenid());
//        detailInput.setOutDetailNo(outTradeNo);
//        detailInput.setTransferAmount(amount);
//        detailInput.setTransferRemark("贵宾顾客购物返利:" + userProfileDO.getPhone());
//        List<TransferDetailInput> detailInputList = new ArrayList<>();
//        detailInputList.add(detailInput);
//        transferRequest.setTransferDetailList(detailInputList);
//        TransferBatchService transferBatchService = new TransferBatchService.Builder().config(payConfig).build();
//        InitiateBatchTransferResponse initiateBatchTransferResponse = transferBatchService.initiateBatchTransfer(transferRequest);
//        log.info("transfer response = {} ", JSON.toJSONString(initiateBatchTransferResponse));
    }

    @Override
    public boolean queryOrderStatus(QueryOrderStatusRequest request) {
        LambdaQueryWrapper<GoodsHistoryDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoodsHistoryDO::getTradeNo, request.getTradeNo());
        GoodsHistoryDO goodsHistoryDO = goodsHistoryMapper.selectOne(queryWrapper);
        Integer isPaySuccess = goodsHistoryDO.getIsPaySuccess();
        return Objects.equals(CompleteEnum.TRUE.getCode(), isPaySuccess);
    }

    @Override
    public void closeOrder(CloseOrderRequest request) throws IOException {
//        com.wechat.pay.java.service.payments.jsapi.model.CloseOrderRequest requestWx = new com.wechat.pay.java.service.payments.jsapi.model.CloseOrderRequest();
//        requestWx.setOutTradeNo(request.getTradeNo());
//        requestWx.setMchid(Constants.MERCHANT_ID);
//        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(payConfig).build();
//        service.closeOrder(requestWx);
    }

    @Override
    public void addShoppingCar(AddShoppingCarRequest request) {
        UserProfileDO userProfileDO = tokenService.getUserByToken(request.getToken());
        if (Objects.isNull(userProfileDO)) {
            throw new BizException("请先登录后查看");
        }

        LambdaQueryWrapper<ShoppingCarDO> carQuery = new LambdaQueryWrapper<>();
        carQuery.eq(ShoppingCarDO::getGoodsId, request.getGoodsId());
        carQuery.eq(ShoppingCarDO::getUserId, userProfileDO.getId());
        ShoppingCarDO shoppingCarOldDO = shoppingCarMapper.selectOne(carQuery);
        if (Objects.nonNull(shoppingCarOldDO)) {
            shoppingCarOldDO.setNum(request.getNum() == null ? shoppingCarOldDO.getNum() + 1 : request.getNum() + shoppingCarOldDO.getNum());
            shoppingCarMapper.updateById(shoppingCarOldDO);
            return;
        }

        ShoppingCarDO shoppingCarDO = new ShoppingCarDO();
        shoppingCarDO.setUserId(userProfileDO.getId());
        shoppingCarDO.setGoodsId(request.getGoodsId());
        shoppingCarDO.setNum(request.getNum() == null ? 1L : request.getNum());
        shoppingCarDO.setModifyTime(new Date());
        shoppingCarDO.setCreateTime(new Date());
        shoppingCarMapper.insert(shoppingCarDO);
    }

    @Override
    public void editShoppingCarNum(AddShoppingCarRequest request) {
        LambdaQueryWrapper<ShoppingCarDO> carQuery = new LambdaQueryWrapper<>();
        carQuery.eq(ShoppingCarDO::getId, request.getId());
        ShoppingCarDO shoppingCarDO = new ShoppingCarDO();
        shoppingCarDO.setNum(request.getNum());
        shoppingCarMapper.update(shoppingCarDO, carQuery);
    }

    @Override
    public List<QueryGoodsByIdResponse> queryGoodsById(QueryGoodsByIdRequest request) {
        LambdaQueryWrapper<GoodsDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoodsDO::getGoodsUnit, request.getGoodsUnit());
        List<GoodsDO> goodsDOS = goodsMapper.selectList(queryWrapper);

        List<QueryGoodsByIdResponse> responseList = new ArrayList<>();
        for (GoodsDO goodsDO : goodsDOS) {
            QueryGoodsByIdResponse response = new QueryGoodsByIdResponse();
            BeanUtils.copyProperties(goodsDO, response);
//            List<GoodsDO> sameGoodsUnitCollections = goodsDOS.stream().filter(goodsDO1 -> goodsDO1.getGoodsUnit().equals(goodsDO.getGoodsUnit()))
//                    .collect(Collectors.toList());
            List<String> goodsPics = Collections.singletonList(goodsDO.getGoodsPic()); // sameGoodsUnitCollections.stream().map(GoodsDO::getGoodsPic).distinct().collect(Collectors.toList());
            response.setGoodsPics(goodsPics); // 商品主图
            List<String> brandPics = Collections.singletonList(goodsDO.getBrandPic());// sameGoodsUnitCollections.stream().map(GoodsDO::getBrandPic).distinct().collect(Collectors.toList());
            response.setBrandPics(brandPics); // 品牌主图
            response.setInventory(goodsDO.getInventory());
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public List<QueryGoodsModel> getGoodsByName(GetGoodsByNameRequest request) {
        LambdaQueryWrapper<GoodsDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(GoodsDO::getName, request.getName());
        List<GoodsDO> goodsDOList = goodsMapper.selectList(queryWrapper);
        List<QueryGoodsModel> queryGoodsModelList = new ArrayList<>();
        for (GoodsDO goodsDO : goodsDOList) {
            QueryGoodsModel model = new QueryGoodsModel();
            BeanUtils.copyProperties(goodsDO, model);
            queryGoodsModelList.add(model);
        }
        return queryGoodsModelList;
    }

    @Override
    public List<QueryCarOrdersResponse> queryCarOrder(AddShoppingCarRequest request) {
        // 查询出对应的用户信息和商品信息
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getToken, request.getToken());
        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);
        if (Objects.isNull(userProfileDO)) {
            throw new BizException("请登录后查看");
        }

        LambdaQueryWrapper<ShoppingCarDO> carQuery = new LambdaQueryWrapper<>();
        carQuery.eq(ShoppingCarDO::getUserId, userProfileDO.getId());
        List<ShoppingCarDO> shoppingCarDOS = shoppingCarMapper.selectList(carQuery);
        List<QueryCarOrdersResponse> responseList = new ArrayList<>();
        for (ShoppingCarDO shoppingCarDO : shoppingCarDOS) {
            QueryCarOrdersResponse queryCarOrdersResponse = new QueryCarOrdersResponse();
            queryCarOrdersResponse.setId(shoppingCarDO.getId());
            queryCarOrdersResponse.setNum(shoppingCarDO.getNum());
            queryCarOrdersResponse.setGoodsId(shoppingCarDO.getGoodsId());
            GoodsDO goodsDO = goodsMapper.selectById(shoppingCarDO.getGoodsId());
            if (Objects.isNull(goodsDO)) {
                continue;
            }
            queryCarOrdersResponse.setName(goodsDO.getName());
            queryCarOrdersResponse.setDescription(goodsDO.getDescription());
            queryCarOrdersResponse.setGoodsPic(goodsDO.getGoodsPic());
            queryCarOrdersResponse.setPrice(goodsDO.getPrice());
            queryCarOrdersResponse.setTotalPrice(shoppingCarDO.getNum() * goodsDO.getPrice());
            queryCarOrdersResponse.setSpecifications(goodsDO.getSpecifications());
            responseList.add(queryCarOrdersResponse);
        }
        return responseList;
    }

    @Override
    public void deleteCarOrderById(AddShoppingCarRequest request) {
        shoppingCarMapper.deleteById(request.getId());

    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public QueryOrderHistoryModel getOrderDetailById(GetOrderDetailByTradeNo request) throws JsonProcessingException {
        LambdaQueryWrapper<GoodsHistoryDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoodsHistoryDO::getTradeNo, request.getTradeNo());
        List<GoodsHistoryDO> goodsHistoryDOList = goodsHistoryMapper.selectList(queryWrapper);

        if (goodsHistoryDOList == null || goodsHistoryDOList.isEmpty()) {
            throw new BizException("订单不存在");
        }

        List<QueryOrderGoodsModel> queryOrderGoodsModelList = new ArrayList<>();
        for (GoodsHistoryDO goodsHistoryDO : goodsHistoryDOList) {
            String goodsListJsonStr = goodsHistoryDO.getGoodsList();
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
        }

        GoodsHistoryDO goodsHistoryDO1 = goodsHistoryDOList.get(0);
        com.alibaba.fastjson.JSONObject orderInfo = com.alibaba.fastjson.JSON.parseObject(goodsHistoryDO1.getOrderInfo());
        QueryOrderHistoryModel queryOrderHistoryModel = new QueryOrderHistoryModel();
        queryOrderHistoryModel.setOrderTime(goodsHistoryDO1.getCreateTime());
        queryOrderHistoryModel.setLogistics(goodsHistoryDO1.getLogistics());
        queryOrderHistoryModel.setLogisticsId(goodsHistoryDO1.getLogisticsId());
        queryOrderHistoryModel.setTradeNo(goodsHistoryDO1.getTradeNo());
        queryOrderHistoryModel.setGoodsModelList(queryOrderGoodsModelList);
        queryOrderHistoryModel.setIsComplete(goodsHistoryDO1.getIsComplete());
        queryOrderHistoryModel.setStatus(goodsHistoryDO1.getStatus());
        queryOrderHistoryModel.setTotalPrice(calculateTotalPrice(queryOrderGoodsModelList));
        queryOrderHistoryModel.setAddr(orderInfo.getString("addr"));
        queryOrderHistoryModel.setPhone(orderInfo.getString("phone"));
        queryOrderHistoryModel.setUserName(orderInfo.getString("name"));
        queryOrderHistoryModel.setPayAmount(goodsHistoryDO1.getPayAmount());
        queryOrderHistoryModel.setIsPaySuccess(goodsHistoryDO1.getIsPaySuccess());
        queryOrderHistoryModel.setPayWay(goodsHistoryDO1.getPayWay());
        return queryOrderHistoryModel;
    }

    public static BigDecimal calculateTotalPrice(List<QueryOrderGoodsModel> queryOrderGoodsModelList) {
        return queryOrderGoodsModelList.stream()
                .filter(Objects::nonNull) // 过滤掉 null 元素
                .map(goods -> goods.getPrice().multiply(BigDecimal.valueOf(goods.getNum())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.DOWN);
    }

    @Override
    public CommitOrderResponse commitOrder(OrderGoodsRequest request) {
        // 查询出对应的用户信息和商品信息
//        String openid = userTokenService.getOpenidByToken(request.getToken());
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(UserProfileDO::getOpenid, openid);
        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);
        if (Objects.isNull(userProfileDO)) {
            throw new BizException("openid is error");
        }

        double weight = 0;
        double price = 0;
        long point = 0;
        long realPoint = 0;
        int num = 0;
        for (OrderGoodsModelRequest modelRequest : request.getModelRequestList()) {
            GoodsDO goodsDO = goodsMapper.selectById(modelRequest.getGoodsId());
//            GoodsTypeDO goodsTypeDO = goodsTypeMapper.selectById(goodsDO.getTypeId());
            Double goodsDOPrice = goodsDO.getPrice();
//            if (Objects.nonNull(goodsTypeDO) && "辅销品".equals(goodsTypeDO.getType())) {
//                realPoint += (long) (goodsDO.getPrice() * 10 * modelRequest.getNum());
//            } else {
//                point += (long) (goodsDO.getPrice() * 10 * modelRequest.getNum());
//            }
//            weight += goodsDO.getWeight() * modelRequest.getNum();
            price += goodsDOPrice * modelRequest.getNum();

        }

        BigDecimal newDecimal = new BigDecimal(0);
        // 邮寄才需要计算邮费（null是为了兼容之前接口逻辑）
        if (Objects.isNull(request.getLogisticsType()) || request.getLogisticsType() == 1) {
            UserAddrDO userAddrDO = userAddrMapper.selectById(request.getAddrId());
            // 订单小于2公斤加0.25，大于2公斤加0.5
            if (weight <= 2) {
                weight = weight + 0.25;
            } else {
                weight = weight + 0.5;
            }
            Double logisticsPrice = LogisticsUtil.getLogisticsPrice((int) Math.ceil(weight), userAddrDO.getProvince());
            if (Objects.isNull(logisticsPrice)) {
                throw new BizException("请输入正确的地址信息");
            }
            // 所有膏方、果蔬饮、祛湿清脂贴 数量总和 0～2 +2，3～4 +2.5，5～8 +3，8～12 +4，12以上+5
            logisticsPrice = addOtherMoneyByNum(num, logisticsPrice);
            BigDecimal oldDecimal = new BigDecimal(logisticsPrice);
            newDecimal = oldDecimal.setScale(1, RoundingMode.HALF_UP);
        }
        realPoint += (long) (newDecimal.doubleValue() * 10);

        CommitOrderResponse response = new CommitOrderResponse();
        response.setPrice(price);
        response.setLogisticsPrice(newDecimal.doubleValue());
        response.setTotalPrice(price + newDecimal.doubleValue());
        response.setPoint(point);
        response.setRealPoint(realPoint);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateUserAddr(UserAddrRequest request) throws Exception {
        // 查询出对应的用户信息
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getToken, request.getToken());
        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);

        // 设置默认地址
        if (request.getIsDefault().equals("1")) {
            UserAddrDO userAddrDO = new UserAddrDO();
            userAddrDO.setIsDefault("2");
            LambdaQueryWrapper<UserAddrDO> addrQuery = new LambdaQueryWrapper<>();
            addrQuery.eq(UserAddrDO::getUserId, userProfileDO.getId());
            userAddrMapper.update(userAddrDO, addrQuery);
        }

        UserAddrDO userAddrDO = new UserAddrDO();
        userAddrDO.setUserId(userProfileDO.getId());
        userAddrDO.setName(request.getName());
        userAddrDO.setPhone(request.getPhone());
        userAddrDO.setIsDefault(request.getIsDefault());
        userAddrDO.setProvince(request.getProvince());
        userAddrDO.setCity(request.getCity());
        userAddrDO.setArea(request.getArea());
        userAddrDO.setDetail(request.getDetail());
        if (Objects.isNull(request.getId())) {
            userAddrDO.setCreateTime(new Date());
            userAddrDO.setModifyTime(new Date());
            userAddrMapper.insert(userAddrDO);
        } else {
            userAddrDO.setModifyTime(new Date());
            userAddrDO.setId(request.getId());
            userAddrMapper.updateById(userAddrDO);
        }
    }

    @Override
    public void deleteUserAddr(UserAddrRequest request) {
        userAddrMapper.deleteById(request.getId());
    }

    @Override
    public List<UserAddrDO> selectUserAddrList(UserAddrRequest request) {
        // 查询出对应的用户信息和商品信息
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getToken, request.getToken());
        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);

        LambdaQueryWrapper<UserAddrDO> addrQuery = new LambdaQueryWrapper<>();
        addrQuery.eq(UserAddrDO::getUserId, userProfileDO.getId());
        addrQuery.orderByDesc(UserAddrDO::getCreateTime);
        return userAddrMapper.selectList(addrQuery);
    }

    @Override
    @Transactional
    public void setAddrDefaul(UserAddrRequest request) {
        // 查询出对应的用户信息
        LambdaQueryWrapper<UserProfileDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileDO::getToken, request.getToken());
        UserProfileDO userProfileDO = userProfileMapper.selectOne(queryWrapper);

        // 将之前的地址都设置为非默认
        UserAddrDO userAddrDO = new UserAddrDO();
        userAddrDO.setIsDefault("2");
        LambdaQueryWrapper<UserAddrDO> addrQuery = new LambdaQueryWrapper<>();
        addrQuery.eq(UserAddrDO::getUserId, userProfileDO.getId());
        userAddrMapper.update(userAddrDO, addrQuery);

        UserAddrDO newUserAddr = userAddrMapper.selectById(request.getId());
        userAddrDO.setIsDefault("1");
        userAddrMapper.updateById(newUserAddr);
    }

    @Override
    @Transactional
    public String returnOrder(OrderRequest request) {
        LambdaQueryWrapper<GoodsHistoryDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoodsHistoryDO::getTradeNo, request.getTradeNo());
        List<GoodsHistoryDO> goodsHistoryDOS = goodsHistoryMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(goodsHistoryDOS)) {
            return "订单不存在";
        }

        GoodsHistoryDO goodsHistoryDO = goodsHistoryDOS.get(0);
//        if (goodsHistoryDO.getIsPack() == 2) {
//            return "订单已打包，无法退货";
//        }

        UserProfileDO userProfileDO = userProfileMapper.selectById(goodsHistoryDO.getUserId());
//        userProfileDO.setPoint(userProfileDO.getPoint() + goodsHistoryDO.getPoint());
//        userProfileDO.setRealPoint(userProfileDO.getRealPoint() + goodsHistoryDO.getRealPoint());
//        userProfileDO.setCostPoint(userProfileDO.getCostPoint() - goodsHistoryDO.getPoint());
//        userProfileDO.setCostRealPoint(userProfileDO.getCostRealPoint() - goodsHistoryDO.getRealPoint());
        userProfileMapper.updateById(userProfileDO);

        // 记录订单使用积分、其他积分
        LambdaQueryWrapper<GoodsHistoryDO> hisQueryWrapper = new LambdaQueryWrapper<>();
        hisQueryWrapper.eq(GoodsHistoryDO::getTradeNo, request.getTradeNo());
        GoodsHistoryDO newHisDO = new GoodsHistoryDO();
//        newHisDO.setIsReturn(2);
        goodsHistoryMapper.update(newHisDO, hisQueryWrapper);

        return "退货成功";
    }

    @Override
    public void packorder(OrderRequest request) {
        LambdaQueryWrapper<GoodsHistoryDO> hisQueryWrapper = new LambdaQueryWrapper<>();
        hisQueryWrapper.eq(GoodsHistoryDO::getTradeNo, request.getTradeNo());
        GoodsHistoryDO newHisDO = new GoodsHistoryDO();
//        newHisDO.setIsPack(2);
        goodsHistoryMapper.update(newHisDO, hisQueryWrapper);
    }

    @Override
    public int updateOrderStatus(String outTradeNo, OrderStatus orderStatus) {
        if (OrderStatus.WAITING_PAYMENT == orderStatus) {
            return goodsHistoryMapper.updateByTradeNo(new GoodsHistoryDO()
                    .setTradeNo(outTradeNo)
                    .setIsPaySuccess(1)
                    .setIsComplete(OrderStatus.WAITING_PAYMENT.getCode())
            );
        } else if (OrderStatus.PAID == orderStatus) {
            return goodsHistoryMapper.updateByTradeNo2(new GoodsHistoryDO()
                    .setTradeNo(outTradeNo)
                    .setIsPaySuccess(2)
                    .setIsComplete(1)
                    .setStatus(OrderStatus.PAID.getCode())
            );
        } else if (OrderStatus.SHIPPED == orderStatus) {
            return goodsHistoryMapper.updateByTradeNo4(new GoodsHistoryDO()
                    .setTradeNo(outTradeNo)
                    .setIsPaySuccess(2)
                    .setIsComplete(1)
                    .setStatus(OrderStatus.SHIPPED.getCode())
            );
        } else if (OrderStatus.COMPLETED == orderStatus) {
            return goodsHistoryMapper.updateByTradeNo6(new GoodsHistoryDO()
                    .setTradeNo(outTradeNo)
                    .setIsPaySuccess(2)
                    .setIsComplete(2)
                    .setStatus(OrderStatus.COMPLETED.getCode())
            );
        } else if (OrderStatus.REFUNDING == orderStatus) {
            return goodsHistoryMapper.updateByTradeNo2(new GoodsHistoryDO()
                    .setTradeNo(outTradeNo)
                    .setIsPaySuccess(2)
                    .setIsComplete(1)
                    .setStatus(OrderStatus.REFUNDING.getCode())
            );
        } else if (OrderStatus.REFUNDED == orderStatus) {
            return goodsHistoryMapper.updateByTradeNo8(new GoodsHistoryDO()
                    .setTradeNo(outTradeNo)
                    .setIsPaySuccess(2)
                    .setIsComplete(1)
                    .setStatus(OrderStatus.REFUNDED.getCode())
            );
        }
        return 0;
    }

    @Override
    public void updatePayway(String tradeNo, PayWayEnums paywayEnums) {
        int updated = goodsHistoryMapper.updatePayWayByTradeNo(new GoodsHistoryDO()
                .setTradeNo(tradeNo)
                .setPayWay(paywayEnums.getValue())
        );
        if (updated > 0) {
            log.info("update payway success");
        }
    }

    @Override
    public ShopCartStatResponse shopCartClassificationStat(ShopCartClassificationStatRequest request) {
        String token = request.getToken();
        if (StringUtils.isBlank(token)) {
            throw new BizException("没有登录");
        }
        UserProfileDO userProfileDO = tokenService.getUserByToken(token);
        if (Objects.isNull(userProfileDO)) {
            throw new BizException("token 错误");
        }

        return new ShopCartStatResponse(
                goodsHistoryMapper.selectCount(new LambdaQueryWrapper<GoodsHistoryDO>()
                        .eq(GoodsHistoryDO::getUserId, userProfileDO.getId())
                        .eq(GoodsHistoryDO::getIsPaySuccess, 1)), // 待付款 1
                goodsHistoryMapper.selectCount(new LambdaQueryWrapper<GoodsHistoryDO>()
                        .eq(GoodsHistoryDO::getUserId, userProfileDO.getId())
                        .eq(GoodsHistoryDO::getIsPaySuccess, 2)
                        .eq(GoodsHistoryDO::getStatus, 3)), // 待发货 3
                goodsHistoryMapper.selectCount(new LambdaQueryWrapper<GoodsHistoryDO>()
                        .eq(GoodsHistoryDO::getUserId, userProfileDO.getId())
                        .eq(GoodsHistoryDO::getIsPaySuccess, 2)
                        .eq(GoodsHistoryDO::getStatus, 4)), // 待收货 4
                goodsHistoryMapper.selectCount(new LambdaQueryWrapper<GoodsHistoryDO>()
                        .eq(GoodsHistoryDO::getUserId, userProfileDO.getId())
                        .eq(GoodsHistoryDO::getIsPaySuccess, 2)
                        .eq(GoodsHistoryDO::getIsPaySuccess, 5)), // 已完成 5
                goodsHistoryMapper.selectCount(new LambdaQueryWrapper<GoodsHistoryDO>()
                        .eq(GoodsHistoryDO::getUserId, userProfileDO.getId())
                        .eq(GoodsHistoryDO::getIsPaySuccess, 2)
                        .eq(GoodsHistoryDO::getIsPaySuccess, 6)  // 退货中 6
                        .or()
                        .eq(GoodsHistoryDO::getIsPaySuccess, 7)) // 已退款 7
        );
    }

    @Override
    public void updateSales(Long goodsId, Long num) {
        // 根据商品id 更新商品库存
        GoodsDO goodsDO = goodsMapper.selectById(goodsId);
        goodsMapper.updateById(new GoodsDO().setId(goodsId).setSales(goodsDO.getInventory() - num));
    }

    @Override
    public GoodsHistoryDO queryOrderByTradeNo(String outTradeNo) {
        GoodsHistoryDO goodsHistoryDO = goodsHistoryMapper.selectOne(new LambdaQueryWrapper<GoodsHistoryDO>()
                .eq(GoodsHistoryDO::getTradeNo, outTradeNo));
        if (Objects.nonNull(goodsHistoryDO)) {
            return goodsHistoryDO;
        }
        return null;
    }

    @Override
    public void cancelOrder(OrderRequest request) {
        GoodsHistoryDO goodsHistoryDO = goodsHistoryMapper.selectOne(new LambdaQueryWrapper<GoodsHistoryDO>().eq(GoodsHistoryDO::getTradeNo, request.getTradeNo()));
        if (goodsHistoryDO == null) {
            throw new BizException("订单不存在");
        }
        // cancel order and update order status to 5(complete)
        goodsHistoryMapper.updateById(new GoodsHistoryDO()
                .setId(goodsHistoryDO.getId())
//                .setIsPaySuccess(2)
                .setIsComplete(2)
                .setStatus(5));
    }

    @Override
    public String applyRefund(OrderRequest request) {
        // 根据订单编号 tradeNo,查询订单是否存在
        GoodsHistoryDO goodsHistoryDO = goodsHistoryMapper.selectOne(new LambdaQueryWrapper<GoodsHistoryDO>().eq(GoodsHistoryDO::getTradeNo, request.getTradeNo()));
        if (goodsHistoryDO == null) {
            throw new BizException("订单不存在");
        }
        if (goodsHistoryDO.getIsPaySuccess() != 2) {
            throw new BizException("订单未支付");
        }
//        if (goodsHistoryDO.getIsComplete() != 2) {
//            throw new BizException("订单未完成");
//        }
        goodsHistoryMapper.updateById(new GoodsHistoryDO()
                .setId(goodsHistoryDO.getId())
                .setStatus(6));
        return "订单申请退款中...";
    }

    @Override
    public String confirmReceipt(OrderRequest request) {
        // 确认收货, 更新订单状态 为 5(已完成)
        GoodsHistoryDO goodsHistoryDO = goodsHistoryMapper.selectOne(new LambdaQueryWrapper<GoodsHistoryDO>().eq(GoodsHistoryDO::getTradeNo, request.getTradeNo()));
        if (goodsHistoryDO == null) {
            throw new BizException("订单不存在");
        }
        goodsHistoryMapper.updateById(new GoodsHistoryDO()
                .setId(goodsHistoryDO.getId())
                .setIsComplete(2)
                .setStatus(5));
        return "订单已完成...";
    }

    private Double addOtherMoneyByNum(Integer num, Double logisticsPrice) {
        if (0 < num && num <= 2) {
            logisticsPrice += 2;
        } else if (2 < num && num <= 4) {
            logisticsPrice += 2.5;
        } else if (4 < num && num <= 8) {
            logisticsPrice += 3;
        } else if (8 < num && num <= 12) {
            logisticsPrice += 4;
        } else if (12 < num) {
            logisticsPrice += 5;
        }
        return logisticsPrice;
    }

    @Override
    public Page<OrderListItem> queryOrderList(OrderListRequest request, UserProfileDO userProfile) {
        // 1. 用户身份验证
//        UserProfileDO user = tokenService.getUserByToken(request.getToken());
//        if (user == null) {
//            throw new BizException("无效的token");
//        }

        // 2. 构建分页查询条件
        Page<GoodsHistoryDO> page = new Page<>(request.getPage(), request.getPageSize());
        LambdaQueryWrapper<GoodsHistoryDO> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(request.getStatus())) {
            if (request.getStatus() == 13) {
                queryWrapper.eq(GoodsHistoryDO::getStatus, 6)
                        .or()
                        .eq(GoodsHistoryDO::getStatus, 7);
            } else {
                queryWrapper.eq(GoodsHistoryDO::getStatus, request.getStatus());
            }
        }
        queryWrapper.eq(GoodsHistoryDO::getUserId, userProfile.getId())
                .orderByDesc(GoodsHistoryDO::getCreateTime);


        if ("showOwner".equals(userProfile.getSource())) {
            queryWrapper.eq(GoodsHistoryDO::getId, userProfile.getId());
        }
        // 3. 执行分页查询
        IPage<GoodsHistoryDO> historyPage = goodsHistoryMapper.selectPage(page, queryWrapper);

        // 4. 数据转换
        List<OrderListItem> items = historyPage.getRecords()
                .stream().map(history -> {
                    OrderListItem item = new OrderListItem();
                    BeanUtils.copyProperties(history, item);

                    // 解析商品列表
                    try {
                        String goodsJson = history.getGoodsList()
                                .replace("\\\"", "\"")
                                .replace("\"[", "[")
                                .replace("]\"", "]");
                        List<QueryOrderGoodsModel> goodsList = objectMapper.readValue(
                                goodsJson,
                                new TypeReference<List<QueryOrderGoodsModel>>() {
                                }
                        );
                        item.setGoodsList(goodsList);
                    } catch (JsonProcessingException e) {
                        log.error("解析商品列表失败", e);
                    }

                    // 计算总价
                    if (CollectionUtils.isNotEmpty(item.getGoodsList())) {
                        List<QueryOrderGoodsModel> goodsList = item.getGoodsList();
                        BigDecimal totalPrice = calculateTotalPrice(goodsList);
                        item.setTotalPrice(totalPrice);
                    }
                    return item;
                }).collect(Collectors.toList());

        // 5. 构建响应
        Page<OrderListItem> pageResult = new Page<>(page.getCurrent(),
                page.getSize(), historyPage.getTotal());
        pageResult.setRecords(items);
//        OrderListResponse response = new OrderListResponse();
//        response.setRecords(items);
//        response.setTotal(historyPage.getTotal());
//        response.setPage(request.getPage());
//        response.setLimit(request.getLimit());
        return pageResult;
    }

    @Override
    public GoodsDO getOrderById(Long orderId) {
        return goodsMapper.selectOne(
                new LambdaQueryWrapper<GoodsDO>().eq(GoodsDO::getId, orderId));
    }


}
