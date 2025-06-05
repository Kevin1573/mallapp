package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

@TableName("shop_config")
@Data
@Accessors(chain = true)
public class ShopConfigDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    @NotBlank(message = "联系人不能为空")
    private String contactPerson;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;

    private String fromMall;

    private String shopNameEng;

    private String homePage;

    private String bestSellers;

    private String recommendedGoods;

    private String recommendedGoodTitle;

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

    private String paymentFlag; // 支付标识

    private String logoUri;

}
