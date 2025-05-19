package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@TableName("shop_combination_recommendation")
@Data
@Accessors(chain = true)
public class ShopCombinationRecommendationDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String fromMall;

    private String name;

    private String goodsPic;

    private String goodsPicExt;

    private String specifications;

    private Boolean firstGoods;

    private String goodsUnit;

    private Double price;

    private Date createTime;

    private Date modifyTime;

}
