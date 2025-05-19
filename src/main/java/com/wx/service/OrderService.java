package com.wx.service;


import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wx.common.enums.OrderStatus;
import com.wx.common.enums.PaywayEnums;
import com.wx.common.model.request.*;
import com.wx.common.model.response.*;
import com.wx.orm.entity.UserAddrDO;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    /**
     * 下单商品
     *
     * @param request 用户
     * @return 前端支付使用的参数
     */
    OrderGoodsResponse orderGoods(OrderGoodsRequest request);

    /**
     * 查询商品列表
     *
     * @param request 分页参数
     * @return 商品明细
     */
    QueryGoodsResponse queryGoods(PageQueryGoodsRequest request);

    /**
     * 微信支付回调
     *
     * @param jsonObject 回调参数
     */
    void wxPayCallback(JSONObject jsonObject) throws Exception;

    /**
     * 轮训查询订单支付状态
     *
     * @param request 订单号
     * @return 订单状态
     */
    boolean queryOrderStatus(QueryOrderStatusRequest request);

    /**
     * 关闭订单
     *
     * @param request 商户信息
     */
    void closeOrder(CloseOrderRequest request) throws IOException;

    /**
     * 添加购物车
     *
     * @param request 商品参数
     */
    void addShoppingCar(AddShoppingCarRequest request);

    /**
     * 编辑购物车商品数量
     *
     * @param request 购物车商品信息
     */
    void editShoppingCarNum(AddShoppingCarRequest request);

    /**
     * 根据商品id查询商品详情
     *
     * @param request 商品id
     * @return QueryGoodsByIdResponse
     */
    QueryGoodsByIdResponse queryGoodsById(QueryGoodsByIdRequest request);

    /**
     * 根据商品名称模糊查询商品详情
     *
     * @param request 商品名称
     * @return 商品详情
     */
    List<QueryGoodsModel> getGoodsByName(GetGoodsByNameRequest request);

    /**
     * 查询用户购物车列表
     *
     * @param request 用户信息
     * @return 购物车列表
     */
    List<QueryCarOrdersResponse> queryCarOrder(AddShoppingCarRequest request);

    /**
     * 删除购物车商品
     *
     * @param request 根据id删除购物车商品
     */
    void deleteCarOrderById(AddShoppingCarRequest request);

    /**
     * 查询订单详情
     *
     * @param request 根据订单id查询订单详情
     * @return 订单明细
     */
    QueryOrderHistoryModel getOrderDetailById(GetOrderDetailByTradeNo request) throws JsonProcessingException;

    /**
     * 提交订单
     *
     * @param request 订单信息
     * @return 价格信息
     */
    CommitOrderResponse commitOrder(OrderGoodsRequest request);

    /**
     * 新增或更新用户收获地址
     *
     * @param request 地址信息
     */
    void addOrUpdateUserAddr(UserAddrRequest request) throws Exception;

    /**
     * 删除用户地址
     * @param request
     */
    void deleteUserAddr(UserAddrRequest request);

    /**
     * 查询用户地址列表
     * @param request
     * @return
     */
    List<UserAddrDO> selectUserAddrList(UserAddrRequest request);

    /**
     * 设置默认地址
     * @param request
     */
    void setAddrDefaul(UserAddrRequest request);

    /**
     * 添加地址时的自动识别功能
     * @param request
     * @return
     * @throws Exception
     */
    MatchAddrResponse match(UserAddrRequest request) throws Exception;

    /**
     * 退货
     * @param request
     * @return
     */
    String returnOrder(OrderRequest request);

    /**
     * 订单打包
     * @param request
     */
    void packorder(OrderRequest request);

    void updateOrderStatus(String outTradeNo, OrderStatus orderStatus);

    void updatePayway(String tradeNo, PaywayEnums paywayEnums);
}
