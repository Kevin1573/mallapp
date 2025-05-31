package com.wx.common.model.response;

import com.wx.common.model.request.QueryOrderGoodsModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class QueryOrderHistoryModel {

    /**
     * 下单时间
     */
    private Date orderTime;
    // 已支付成功，1未支付，2支付成功
    private Integer isPaySuccess;

    // 1 wx  2 alipay
    private Integer payWay;
    /**
     * 定单是否发货完成，1未完成 2完成
     */
    private Integer isComplete;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 物流信息
     */
    private String logistics;

    /**
     * 物流订单号
     */
    private String logisticsId;

    private String tradeNo;

    private BigDecimal totalPrice;

    /**
     * 收件人昵称
     */
    private String userName;

    /**
     * 下单人昵称
     */
    private String realName;

    private String addr;

    /**
     * 收件人号码
     */
    private String phone;

    /**
     * 下单人号码
     */
    private String realPhone;

    private List<QueryOrderGoodsModel> goodsModelList;

    private BigDecimal payAmount;

}

