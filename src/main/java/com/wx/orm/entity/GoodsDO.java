package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    private String brandUrl;

    /**
     * 品牌主图
     */
    @TableField(value = "brand_pic")
    private String brandPic;

    /**
     * 是否使用卡券
     */
    private Integer card;

    /**
     * 库存
     */
    private long inventory;

    /**
     * 规格
     */
    private String specifications;

    /**
     * 销量
     */
    private Long sales;

    /**
     * 是否首次创建
     */
    private Boolean firstGoods;

    /**
     * 分类
     */
    private String category;

    /**
     * 同一款商品标示
     */
    private String goodsUnit;

    private Integer status; // 0-下架 1-上架

    private String fromMall;

    private Integer recommendGoods;
}
