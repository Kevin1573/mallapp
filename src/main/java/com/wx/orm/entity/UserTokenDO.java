package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@TableName("user_token")
@Data
@Accessors(chain = true)
public class UserTokenDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 微信返回的用户唯一标识
     */
    private String openid;

    private String token;

    private Date createTime;

    private Date modifyTime;
}
