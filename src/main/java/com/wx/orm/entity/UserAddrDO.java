package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@TableName("user_addr")
@Data
@Accessors(chain = true)
public class UserAddrDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 地址所属用户id
     */
    private Long userId;

    /**
     * 收货人姓名
     */
    private String name;

    /**
     * 收货人手机号码
     */
    private String phone;

    private String province;

    private String city;

    private String area;

    private String detail;

    /**
     * 是否是默认地址，1默认2不是默认
     */
    private String isDefault;

    private Date createTime;

    private Date modifyTime;
}
