-- 用户表
CREATE TABLE IF NOT EXISTS `wx_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `open_id` varchar(64) NOT NULL COMMENT '微信openid',
  `unionid` varchar(64) DEFAULT NULL COMMENT '微信unionid',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `city` varchar(64) DEFAULT NULL COMMENT '城市',
  `province` varchar(64) DEFAULT NULL COMMENT '省份',
  `country` varchar(64) DEFAULT NULL COMMENT '国家',
  `create_time` date,
  `update_time` date,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_openid` (`open_id`) COMMENT 'openid唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小程序用户表';

-- 会话信息表
CREATE TABLE IF NOT EXISTS `session_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `token` varchar(64) NOT NULL COMMENT '会话token',
  `openid` varchar(64) NOT NULL COMMENT '用户openid',
  `session_key` varchar(64) NOT NULL COMMENT '微信session_key',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_token` (`token`) COMMENT 'token唯一索引',
  KEY `idx_openid` (`openid`) COMMENT 'openid索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话信息表';
