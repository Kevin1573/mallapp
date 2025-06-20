package com.wx.miniapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("wx_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "open_id")
    private String openid;  // 微信openid

    private String nickname;  // 微信昵称
    private String avatarUrl;  // 微信头像
    private String gender;  // 性别
    private String city;  // 城市
    private String province;  // 省份
    private String country;  // 国家
    private String unionid;  // 微信unionid

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
