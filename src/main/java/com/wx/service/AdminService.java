package com.wx.service;

import com.wx.common.model.request.*;
import com.wx.common.model.response.*;

import java.util.List;

public interface AdminService {

    void initData();

    /**
     * 后台上传商品
     *
     * @param request 商品参数
     */
    void uploadGoods(UploadGoodsRequest request);

    /**
     * 查看订单历史数据
     *
     * @param request 分页查询入参
     * @return 订单数据
     */
    QueryOrderHistoryResponse queryOrderHistory(QueryOrderHistoryRequest request);

    /**
     * 后台完成订单
     *
     * @param request 订单参数
     */
    void completeOrder(CompleteOrderRequest request);

    /**
     * 后台更新订单的物流信息
     *
     * @param request 物流信息
     */
    void updateGoodsHisLogistics(CompleteOrderRequest request);

    /**
     * 根据id删除商品
     *
     * @param request request
     */
    void deleteGoodById(DeleteCommunityRequest request);

    /**
     * 后台更新用户身份信息/积分信息
     *
     * @param request 身份参数
     */
    void updateUserPosition(UpdateUserPositionRequest request);

    /**
     * 修改商品信息
     *
     * @param request 商品价格
     */
    void updateGoods(UpdateGoodsRequest request);

    /**
     * 新增商品分类
     *
     * @param request 商品分类
     */
    void addGoodsType(UploadGoodsRequest request);

    /**
     * 查询全部商品分类
     *
     * @return List<String> 商品分类列表
     */
    List<GoodsTypeResponse> queryGoodsTypeList();

    /**
     * 根据商品分类id删除分类
     *
     * @param request 分类id
     */
    void deleteTypeById(GoodsTypeResquest request);

    /**
     * 查询用户列表
     *
     * @param request 分页参数
     * @return UserProfileModel
     */
    PageQueryMemberResponse queryUserList(PageQueryUserRequest request);

    /**
     * 设置用户返利
     *
     * @param request 用户返利信息
     */
    void updateUserRebate(updateUserRebateRequest request);

    /**
     * 查询用户返利点列表
     *
     * @return List<UserRebateResponse>
     */
    List<UserRebateResponse> queryUserRebateResponse();

    /**
     * 编辑用户积分，增减明细要记录（新积分接口，老接口只用来编辑）
     *
     * @param request 积分明细
     */
    void updateUserPointNew(UserPointNewRequest request);

    /**
     * 查看我的业绩
     *
     * @param request 分页参数以及用户信息
     * @return 业绩列表
     */
    QueryMyPerformanceResponse queryMyPerformance(QueryMyPerformanceRequest request);

    /**
     * 查看用户充值流水
     * @param request
     * @return
     */
    QueryUserPointHisResponse queryMyPointHis(QueryMyPerformanceRequest request);

}
