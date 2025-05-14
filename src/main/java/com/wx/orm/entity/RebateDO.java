package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("rebate")
@Data
public class RebateDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 职位code
     */
    private String positionCode;

    /**
     * 职位中文名称
     */
    private String description;

    /**
     * 返利点
     */
    private Double ratio;

    private Date createTime;

    private Date modifyTime;
}
