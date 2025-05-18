package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 CREATE TABLE `coupon` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
 `name` varchar(50) NOT NULL COMMENT '优惠券名称',
 `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '类型 0:折扣券 1:满减券',
 `value` decimal(10,2) NOT NULL COMMENT '优惠值(折扣率或固定金额)',
 `min_amount` decimal(10,2) DEFAULT '0.00' COMMENT '最低消费金额',
 `product_scope` varchar(255) DEFAULT NULL COMMENT '适用商品ID集合(逗号分隔)',
 `category_id` bigint(20) DEFAULT NULL COMMENT '适用商品分类ID',
 `expire_type` tinyint(1) DEFAULT '0' COMMENT '有效期类型 0:固定时间 1:领取后生效',
 `expire_days` int(11) DEFAULT NULL COMMENT '有效天数(expire_type=1时生效)',
 `expire_start` datetime DEFAULT NULL COMMENT '有效期开始时间',
 `expire_end` datetime DEFAULT NULL COMMENT '有效期结束时间',
 `total_quantity` int(11) NOT NULL COMMENT '发放总量',
 `left_quantity` int(11) NOT NULL COMMENT '剩余数量',
 [status](file://D:\code\mallapp\src\main\java\com\wx\common\model\response\QueryRefundHisModel.java#L9-L9) tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 0:禁用 1:启用',
 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY ([id](file://D:\code\mallapp\src\main\java\com\wx\orm\entity\RebateDO.java#L13-L14)),
 KEY `idx_status` ([status](file://D:\code\mallapp\src\main\java\com\wx\common\model\response\QueryRefundHisModel.java#L9-L9)),
 KEY `idx_expire` (`expire_start`,`expire_end`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

 -- 折扣券（85折，满100可用）
 INSERT INTO coupon (name, type, value, min_amount, expire_start, expire_end, total_quantity, left_quantity)
 VALUES ('夏季折扣', 0, 0.85, 100.00, '2024-06-01', '2024-08-31', 1000, 1000);

 -- 满减券（满200减50）
 INSERT INTO coupon (name, type, value, min_amount, expire_days, total_quantity, left_quantity)
 VALUES ('618大促', 1, 50.00, 200.00, 15, 500, 500);
 */
@TableName("coupon")
@Data
@Accessors(chain = true)
public class CouponDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer type;
    private BigDecimal value;
    private BigDecimal minAmount;
    // 其他字段...
}