package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@TableName("user_profile")
@Data
@Accessors(chain = true)
public class UserProfileDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像链接
     */
    private String headUrl;

    /**
     * 用户职位
     */
    private int position;

    private Date createTime;

    private Date modifyTime;

    /**
     * 店铺名称
     */
    private String from;

    /**
     * 登录标示
     */
    private String token;




}
