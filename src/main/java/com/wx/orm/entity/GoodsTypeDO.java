package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("goods_type")
@Data
public class GoodsTypeDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String type;

    private Date createTime;

    private Date modifyTime;
}
