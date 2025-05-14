package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("home_page_banner")
@Data
public class HomePageBannerDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 首页banner，多个banner逗号间隔
     */
    private String banner;

    private Date createTime;

    private Date modifyTime;
}
