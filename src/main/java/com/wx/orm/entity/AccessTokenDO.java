package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@TableName("access_token")
@Data
@Accessors(chain = true)
public class AccessTokenDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 微信调用凭证
     */
    private String accessToken;

    private Date createTime;

    private Date modifyTime;
}
