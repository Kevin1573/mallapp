package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@TableName("shop_config")
@Data
@Accessors(chain = true)
public class ShopConfigDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String fromMall;

    private String shopName;

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

}
