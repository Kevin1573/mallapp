package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@TableName("shopping_car")
@Data
@Accessors(chain = true)
public class ShoppingCarDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

   private Long userId;

   private Long goodsId;

   private Long num;

    private Date createTime;

    private Date modifyTime;
}
