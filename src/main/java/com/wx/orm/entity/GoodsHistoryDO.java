package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.util.Date;

@TableName("goods_history")
@Data
@Accessors(chain = true)
public class GoodsHistoryDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品id (-1表示是开会员订单，-2表示报修单)
     */
//    private Long goodsId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 定单是否发货完成，1未完成 2完成
     */
    private Integer isComplete;

    /**
     * 是否支付完成，1未完成 2完成
     */
    private Integer isPaySuccess;

    //  订单状态
    private Integer status;

    /**
     * 商品数量
     */
    private Long num;

    /**
     * 订单号
     */
    private String tradeNo;

    private Date createTime;

    private Date modifyTime;

    /**
     * 物流信息
     */
    private String logistics;

    /**
     * 物流订单号
     */
    private String logisticsId;

    /**
     * 商品图片
     */
//    private String goodsPic;

    /**
     * 商品描述
     */
//    private String goodsDescription;

    /**
     * 价格，单位为元
     */
//    private Double goodsPrice;

    /**
     * 商品名称
     */
//    private String goodsName;

    private Long addrId;

    /**
     * 订单收件人信息
     */
    private String orderInfo;

    /**
     * 商品列表 json 格式字符串
     * 使用 @TableField 标注字段类型（非必须但推荐）
     */
    @TableField(value = "goods_list", jdbcType = JdbcType.VARCHAR)
    private String goodsList;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    // 支付方式 1-wxpay 2-alipay
    @TableField(value = "pay_way", jdbcType = JdbcType.TINYINT)
    private Integer payWay;

}
