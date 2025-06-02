package com.wx.common.model.response;

import lombok.Data;

import java.util.Date;

@Data
public class ShopConfigDOResponse {

    private Long id;
    private String shopName;
    private String contactPerson;
    private String contactPhone;
    private String fromMall;

    private String shopNameEng;

    private String homePage;

    private String bestSellers;

    private String aboutUsText;

    private String aboutUsPic;

    private String companyName;

    private String companyPhone;

    private String companyAddr;

    private String programUrl;

    private String wechatUrl;

    private Date createTime;

    private Date modifyTime;

    private double freight;

    private String email;

    private String source;

    private String paymentFlag;

}
