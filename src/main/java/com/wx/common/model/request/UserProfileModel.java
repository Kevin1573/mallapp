package com.wx.common.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class UserProfileModel {

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

    private Long point;

    private Long realPoint;

    private Long costPoint;

    private Long costRealPoint;
}
