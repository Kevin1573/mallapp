package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@TableName("user_point_his")
@Data
@Accessors(chain = true)
public class UserPointHisDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 用户充值积分流水
     */
    private Integer editPoint;

    /**
     * 用户充值其他积分流水
     */
    private Integer editRealPoint;

    /**
     * 用户使用积分流水
     */
    private Integer usedPoint;

    /**
     * 用户使用其他积分流水
     */
    private Integer usedRealPoint;

    private Date createTime;

    private Date modifyTime;
}
