package com.wx.common.model.response;

import com.wx.common.model.request.QueryOrderGoodsModel;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QueryOrderHistoryModel {

    /**
     * 下单时间
     */
    private Date orderTime;

    /**
     * 定单是否发货完成，1未完成 2完成
     */
    private Integer isComplete;

    /**
     * 是否退货；1未退货，2已退货
     */
    private Integer isReturn;

    /**
     * 是否打包；1未打包，2已打包
     */
    private Integer isPack;

    /**
     * 物流信息
     */
    private String logistics;

    /**
     * 物流订单号
     */
    private String logisticsId;

    private String tradeNo;

    private Double totalPrice;

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

}

