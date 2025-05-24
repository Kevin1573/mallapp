package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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

    @JsonIgnore
    @NotBlank(message = "密码不能为空")
    private String password;
    /**
     * 手机号码
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 头像链接
     */
    private String headUrl;

    /**
     * 用户职位,0默认普通用户
     */
    private int position;

    private Date createTime;

    private Date modifyTime;

    /**
     * 店铺名称
     */
    private String fromShopName;

    /**
     * 登录标示
     */
    private String token;

    @NotBlank(message = "用户来源不能为空")
    private String source;

    // 新增字段
    @TableField("last_pwd_update")
    private Date lastPasswordUpdate;

}
