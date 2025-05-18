package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@TableName("goods")
@Data
@Accessors(chain = true)
public class GoodsDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品图片
     */
    private String goodsPic;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 价格，单位为元(保留一位小数)
     */
    private Double price;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品类型id
     */
    private Long typeId;

    private Date createTime;

    private Date modifyTime;

    /**
     * 商品扩展图片
     */
    private String ext;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 是否使用卡券
     */
    private Integer card;

    /**
     * 库存
     */
    private Integer inventory;

    /**
     * 规格
     */
    private String specifications;

    /**
     * 销量
     */
    private Integer sales;

}
