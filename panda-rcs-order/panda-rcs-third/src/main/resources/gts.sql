CREATE TABLE `rcs_gts_order_ext`  (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
`order_no` varchar(100)   DEFAULT NULL COMMENT '订单编号',
`request_json` longtext  DEFAULT NULL COMMENT '请求数据',
`status` varchar(50)   DEFAULT NULL COMMENT '订单状态',
`pa_amount` varchar(50)   DEFAULT NULL COMMENT 'panda收到的金额',
`gts_amount` varchar(50)   DEFAULT NULL COMMENT '给GTS的金额',
`result` text DEFAULT NULL  COMMENT '订单返回结果',
`cre_time` datetime(0) DEFAULT NULL COMMENT '创建时间',
`update_time` datetime(0) DEFAULT NULL COMMENT '修改时间',
`cancel_status` int(11) DEFAULT 0 COMMENT '是否取消 0否  1是',
`cancel_id` int(11) DEFAULT 0 COMMENT '取消编码 101 普通 102 超时  103 后台取消 104 技术问题取消 105 后台异常取消  106 现金返还促销',
`cancel_result` text DEFAULT NULL   COMMENT '取消返回结果',
`third_name` varchar(20)   DEFAULT NULL COMMENT '第三方标志',
`remark` varchar(1200)   DEFAULT NULL COMMENT '备注',
`third_no` varchar(120)   DEFAULT NULL COMMENT '第三方订单号',
PRIMARY KEY (`id`) USING BTREE,
UNIQUE INDEX `uniq_order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB COMMENT 'GTS注单表';
