-- 创建库存锁定记录表
CREATE TABLE IF NOT EXISTS `stock_lock` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
    `order_id` varchar(64) NOT NULL COMMENT '订单ID',
    `lock_quantity` int(11) NOT NULL COMMENT '锁定数量',
    `lock_time` datetime NOT NULL COMMENT '锁定时间',
    `status` varchar(20) NOT NULL COMMENT '锁定状态：LOCKED(已锁定), UNLOCKED(已释放), DEDUCTED(已扣减)',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_goods_id` (`goods_id`),
    KEY `idx_lock_time` (`lock_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存锁定记录表';

-- 修改商品表，添加库存相关字段
ALTER TABLE `goods`
    ADD COLUMN `stock` int(11) NOT NULL DEFAULT '0' COMMENT '当前库存数量',
    ADD COLUMN `locked_stock` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存数量',
    ADD COLUMN `total_stock` int(11) NOT NULL DEFAULT '0' COMMENT '总库存数量',
    ADD COLUMN `stock_warning` int(11) DEFAULT '10' COMMENT '库存预警阈值';

-- 创建库存变更历史表
CREATE TABLE IF NOT EXISTS `stock_history` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
    `change_type` varchar(20) NOT NULL COMMENT '变更类型：IN(入库), OUT(出库), LOCK(锁定), UNLOCK(解锁)',
    `change_quantity` int(11) NOT NULL COMMENT '变更数量',
    `before_stock` int(11) NOT NULL COMMENT '变更前库存',
    `after_stock` int(11) NOT NULL COMMENT '变更后库存',
    `order_id` varchar(64) DEFAULT NULL COMMENT '关联订单ID',
    `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_goods_id` (`goods_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变更历史表';

-- 创建库存预警记录表
CREATE TABLE IF NOT EXISTS `stock_warning_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
    `current_stock` int(11) NOT NULL COMMENT '当前库存',
    `warning_threshold` int(11) NOT NULL COMMENT '预警阈值',
    `status` varchar(20) NOT NULL COMMENT '状态：PENDING(待处理), PROCESSED(已处理)',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `process_time` datetime DEFAULT NULL COMMENT '处理时间',
    `processor` varchar(64) DEFAULT NULL COMMENT '处理人',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_goods_id` (`goods_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存预警记录表';

-- 添加索引以提高查询性能
ALTER TABLE `stock_lock`
    ADD INDEX `idx_status_lock_time` (`status`, `lock_time`);

ALTER TABLE `goods`
    ADD INDEX `idx_stock` (`stock`),
    ADD INDEX `idx_stock_warning` (`stock`, `stock_warning`);
