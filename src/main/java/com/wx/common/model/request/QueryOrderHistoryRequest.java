package com.wx.common.model.request;

import lombok.Data;

@Data
public class QueryOrderHistoryRequest {

    private Long page;

    private Long limit;
//
//    /**
//     * 定单是否完成，1未完成 2完成
//     */
//    private String isComplete;
//
//    /**
//     * 是否退货；1未退货，2已退货
//     */
//    private Integer isReturn;
//
//    /**
//     * 是否打包；1未打包，2已打包
//     */
//
//    private Integer isPack;


    //订单状态，1待付款 2待发货 3待收货 4已完成 5退货单
    private Integer status;
    /**
     * 收件人昵称
     */
    private String nickname;

    private String token;

}
