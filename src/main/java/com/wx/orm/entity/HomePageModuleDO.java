package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("home_page_module")
@Data
public class HomePageModuleDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模块banner
     */
    private String banner;

    private String title;

    /**
     * 模块商品id列表，逗号间隔
     */
    private String goodsList;

    private Date createTime;

    private Date modifyTime;
}
