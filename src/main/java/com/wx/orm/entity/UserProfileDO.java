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
     * 微信返回的用户唯一标识
     */
    private String openid;

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
    private String position;

    /**
     * 用户地址
     */
    private String addr;

    /**
     * 邀请人id
     */
    private Long inviteUserId;

    private Date createTime;

    private Date modifyTime;

    /**
     * 商品积分
     */
    private Long point;

    /**
     * 其他积分，辅销品+快递费积分
     */
    private Long realPoint;

    private Long costPoint;

    private Long costRealPoint;
}
