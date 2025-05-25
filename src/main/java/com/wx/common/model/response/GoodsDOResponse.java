package com.wx.common.model.response;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GoodsDOResponse {
    private Long id;

    /**
     * 商品图片
     */
    private String goodsPic;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;


    private List<GoodsSubDO> subGoodsList;


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
     * 规格
     */
    private String specifications;


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

    @Data
    public static class GoodsSubDO {
        /**
         * 价格，单位为元(保留一位小数)
         */
        private Double price;

        /**
         * 库存
         */
        private long inventory;
        /**
         * 销量
         */
        private Long sales;

        private String goodsPic;

    }
}
