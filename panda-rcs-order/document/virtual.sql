
CREATE TABLE `rcs_virtual_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` BIGINT(20) NOT NULL COMMENT 'panda系统用户ID',
  `virtual_user_id` INT(11) NOT NULL COMMENT '第三方用户ID',
  `virtual_user_name` VARCHAR(20) NOT NULL COMMENT '第三方用户名(对应panda的userid)',
  `virtual_ext_id` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '第三方ext_id',
  `virtual_user_status` VARCHAR(20) NOT NULL COMMENT '第三方用户状态',
  `virtual_parent_id` INT(11) NOT NULL COMMENT '第三方用户上级ID',
  `calculation_id` INT(11) NOT NULL COMMENT '下注需要使用到的ID',
  `wallet_id` BIGINT(20) NOT NULL COMMENT '钱包ID',
  `create_time` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `panda_status` INT(11) NOT NULL DEFAULT '1' COMMENT 'pandan备用字段 用户状态(0 禁用 1启用)',
  `remark` TEXT COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_virtual_user_id` (`virtual_user_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='虚拟赛事-第三方用户信息表';



CREATE TABLE `rcs_virtual_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(20) NOT NULL COMMENT 'panda系统用户ID',
  `virtual_user_id` int(11) NOT NULL COMMENT '第三方用户ID',
  `order_no` varchar(64) NOT NULL DEFAULT '' COMMENT 'panda订单号',
  `ticket_id` bigint(20) DEFAULT '0' COMMENT '第三方订单号(订单成功了才会返回)',
  `transaction_id` bigint(20) DEFAULT '0' COMMENT '第三方transactionId(订单成功了才会返回)',
  `request_param` text COMMENT '请求第三方参数',
  `response_param` text COMMENT '第三方返回结果',
  `response_status` varchar(50) DEFAULT NULL COMMENT '响应状态',
  `order_status` int(11) NOT NULL DEFAULT '-1' COMMENT '订单状态: -1 初始化(处理中) 0 失败  1 成功',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注(主要记录失败原因)',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_no` (`order_no`),
  KEY `idx_ticket_id` (`ticket_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='虚拟赛事-第三方订单记录';