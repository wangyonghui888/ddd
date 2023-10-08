
ALTER TABLE tybss_new.t_order_detail ADD INDEX idx_uid(uid);

DROP TABLE if exists user_special_statis;
CREATE TABLE `user_special_statis` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `statis_day` BIGINT(20) DEFAULT NULL COMMENT   '统计日期 存当天的0点0分0秒',
  `user_id` BIGINT(20) DEFAULT NULL COMMENT    '用户ID',
  `parent_type` INT(11) DEFAULT NULL COMMENT   '父统计类型:1球类,2联赛,3玩法,4球队,5盘口类型,6赔率,7投注金额,8正副盘,9对冲投注',
  `child_type` varchar (64) DEFAULT NULL COMMENT
  '子统计类型(分组字段):
	父1 :存sportId  值1表示 球类下面的足球 ,
	父2 :存联赛id   
	父3 :存sportid-playid(用"-"隔开),
	父4 :存球队id, 
	父5 :存盘口类型, 
	父6 :1对应[1-1.3 )、2对应[1.3-1.5 )、3对应[1.5-2 )、4对应[2,3 )、5对应[3,5 )、6对应[5,10 )、7对应>10, 
	父7 :1对应<1000、2对应1000,2000 )、3对应[2000,5000 )、4对应[5000,10000 )、5对应>10000, 
	父8 :0正盘,1副盘,
	父9 :1是 0否,  ',
  `value` BIGINT(20) DEFAULT NULL COMMENT '值:投注金额(仅在parent_type为4时,表示球队次数)',
  `profit` BIGINT(20) DEFAULT NULL COMMENT '盈利金额',
  `bet_num` BIGINT(20) DEFAULT NULL COMMENT '投注总笔数',
  `bet_profit_num` BIGINT(20) DEFAULT NULL COMMENT '盈利投注笔数', 
  PRIMARY KEY (`id`), 
  KEY `idx_user_id` (`user_id`),
  KEY `idx_statis_day` (`statis_day`),
  KEY `idx_parent_type` (`parent_type`)
) ENGINE=INNODB   COMMENT '用户行为详情-投注偏好/财务特征-日统计表';

alter table user_special_statis add column  `finance_value` BIGINT(20) DEFAULT NULL COMMENT '财务特征统计:投注金额(只包含已结算的)';


DROP TABLE if exists risk_user_visit_ip;
CREATE TABLE `risk_user_visit_ip` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) DEFAULT NULL COMMENT '用户Id',
  `tag_id` BIGINT(20) DEFAULT NULL COMMENT 'risk_user_visit_ip_tag表Id',
  `login_date` BIGINT(20) DEFAULT NULL COMMENT '登录日期',
  `ip` VARCHAR(20) DEFAULT NULL COMMENT 'ip地址',
  `area` VARCHAR(100) DEFAULT NULL COMMENT '地区',
  `country` VARCHAR(100) DEFAULT NULL COMMENT '国家',
  `province` VARCHAR(100) DEFAULT NULL COMMENT '省',
  `city` VARCHAR(100) DEFAULT NULL COMMENT '市',
  PRIMARY KEY (`id`), 
  KEY `idx_user_id` (`user_id`),
  KEY `idx_ip` (`ip`),
  KEY `idx_login_date` (`login_date`)
) ENGINE=INNODB   COMMENT '用户行为详情-访问特征-用户登录ip记录表';

DROP TABLE if exists risk_user_visit_ip_tag;
CREATE TABLE `risk_user_visit_ip_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT, 
  `tag` VARCHAR(20) DEFAULT NULL COMMENT '标签',
  `create_time` BIGINT(20) DEFAULT NULL  COMMENT '创建时间 存当天的0点0分0秒',
  PRIMARY KEY (`id`)
) ENGINE=INNODB   COMMENT 'ip标签表';

DROP TABLE if exists user_profile_rule;
CREATE TABLE `user_profile_rule` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `rule_code` VARCHAR(20) DEFAULT NULL COMMENT '规则id',
  `rule_type` INT(11) DEFAULT NULL COMMENT '规则类型 1, 基本属性类 2, 投注特征类 3, 访问特征类 4, 财务特征类 ',
  `rule_name` VARCHAR(50) DEFAULT NULL COMMENT '规则名',
  `rule_detail` VARCHAR(500) DEFAULT NULL COMMENT '规则说明',
  `parameter1` VARCHAR(50) DEFAULT NULL COMMENT '参数1',
  `parameter2` VARCHAR(50) DEFAULT NULL COMMENT '参数2',
  `parameter3` VARCHAR(50) DEFAULT NULL COMMENT '参数3',
  `parameter4` VARCHAR(50) DEFAULT NULL COMMENT '参数4',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_rule_code` (`rule_code`)
) ENGINE=INNODB   COMMENT='规则管理表';

DROP TABLE if exists user_profile_dangerous_bet_rule;
CREATE TABLE `user_profile_dangerous_bet_rule` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `dangerous_code` VARCHAR(20) DEFAULT NULL COMMENT '危险投注标识id',
  `sport_id` INT DEFAULT NULL COMMENT '所属球类 0全部  非0表示对应的体育id',
  `rule_name` VARCHAR(20) DEFAULT NULL COMMENT '名称',
  `rule_detail` VARCHAR(500) DEFAULT NULL COMMENT '定义',  
  `parameter1` VARCHAR(50) DEFAULT NULL COMMENT '参数1',
  `parameter2` VARCHAR(50) DEFAULT NULL COMMENT '参数2',
  `parameter3` VARCHAR(50) DEFAULT NULL COMMENT '参数3',
  `parameter4` VARCHAR(50) DEFAULT NULL COMMENT '参数4',
  `enable` INT DEFAULT 0 COMMENT '是否启用 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_dangerous_code` (`dangerous_code`)
) ENGINE=INNODB   COMMENT '危险投注表';

DROP TABLE if exists user_profile_tags;
CREATE TABLE `user_profile_tags` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT, 
  `tag_type` INT DEFAULT NULL COMMENT '标签类型 1, 基本属性类 2, 投注特征类 3, 访问特征类 4, 财务特征类 ',
  `tag_name` VARCHAR(20) DEFAULT NULL COMMENT '标签名称',
  `tag_detail` VARCHAR(500) DEFAULT NULL COMMENT '标签说明', 
  PRIMARY KEY (`id`)
) ENGINE=INNODB   COMMENT '用户画像标签管理表';

DROP TABLE if exists user_profile_tags_rule_relation;
CREATE TABLE `user_profile_tags_rule_relation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT, 
  `tag_id` BIGINT DEFAULT NULL COMMENT  '标签id',
  `rule_id` BIGINT DEFAULT NULL COMMENT '规则id',
  `parameter1` VARCHAR(50) DEFAULT NULL COMMENT '规则参数1',
  `parameter2` VARCHAR(50) DEFAULT NULL COMMENT '规则参数2',
  `parameter3` VARCHAR(50) DEFAULT NULL COMMENT '规则参数3',
  `parameter4` VARCHAR(50) DEFAULT NULL COMMENT '规则参数4',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tag_id_rule_id` (`tag_id`,`rule_id`)
) ENGINE=INNODB   COMMENT '标签-规则关系表';

DROP TABLE if exists user_profile_tag_user_relation;
CREATE TABLE `user_profile_tag_user_relation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT, 
  `tag_id` BIGINT DEFAULT NULL COMMENT  '标签id',
  `user_id` BIGINT DEFAULT NULL COMMENT '规则id',
  `status` INT DEFAULT 0 COMMENT  '变更状态:0 等待手工确认  1 已确认',
  PRIMARY KEY (`id`), 
  KEY `idx_user_id` (`user_id`)
) ENGINE=INNODB   COMMENT '标签-用户关系表';

DROP TABLE if exists user_profile_user_tag_change_record;
CREATE TABLE `user_profile_user_tag_change_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT, 
  `user_id` BIGINT(20) DEFAULT NULL COMMENT    '用户ID',
  `change_time` BIGINT(20) DEFAULT NULL  COMMENT '变更时间',
  `change_type` INT DEFAULT NULL COMMENT '变更方式 1自动,2手动',
  `change_manner` VARCHAR(20) DEFAULT NULL COMMENT '变更人(手动情况下)',
  `change_before` BIGINT DEFAULT NULL COMMENT '变更前标签',
  `change_after` BIGINT DEFAULT NULL COMMENT '变更后标签',
  `change_detail` VARCHAR(500) DEFAULT NULL COMMENT '变更说明', 
  `change_value` VARCHAR(500) DEFAULT NULL COMMENT '变更值 记录每个规则的真实值', 
  PRIMARY KEY (`id`), 
  KEY `idx_user_id` (`user_id`)
) ENGINE=INNODB   COMMENT '标签变更记录表 每次变更只记录一条 '; 

DROP TABLE if exists user_profile_order_tag;
CREATE TABLE `user_profile_order_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT, 
  `bet_no` VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '注单编号',
  `order_no` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '订单编号',
   is_inverse INT DEFAULT 0  COMMENT '是否对冲投注 0否 1是',
  PRIMARY KEY (`id`), 
  KEY `idx_bet_no` (`bet_no`), 
  KEY `idx_order_no` (`order_no`)
) ENGINE=INNODB   COMMENT '订单标签扩展表,标识订单部分特殊字段';

DROP TABLE if exists user_profile_order_dangerous;
CREATE TABLE `user_profile_order_dangerous` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `bet_no` VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '注单编号',
  `order_no` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '订单编号',
   market_id BIGINT(20)  DEFAULT 0  COMMENT '盘口id ',
   play_options_id BIGINT(20)  DEFAULT 0  COMMENT '投注项id ',
   dangerous_id INT DEFAULT 0 COMMENT '对应的危险规则id ',
   create_time BIGINT(20)  DEFAULT 0  COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_bet_no` (`bet_no`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=INNODB   COMMENT '注单-危险投注关系表';

DROP TABLE IF EXISTS `order_option_odd_change`;
CREATE TABLE `order_option_odd_change`  (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '表ID，自增',
  `bet_no` VARCHAR(50)    NOT NULL DEFAULT '' COMMENT '注单编号',
  `order_no` VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '订单编号',
  `play_options_id` BIGINT(11) NOT NULL DEFAULT 0 COMMENT '投注项ID.取自订单',
  `odds_value` INT(11) NOT NULL DEFAULT 0 COMMENT '订单赔率. 单位: 0.0001',
  `order_type` INT(11) NOT NULL DEFAULT 0 COMMENT '0:赛前盘;1:滚球',
  `mark` INT(11) NOT NULL DEFAULT 0 COMMENT '是否已经处理;0:未处理;1:处理',
  `remark` VARCHAR (50) NULL DEFAULT '' COMMENT '备注',
  `bet_time` BIGINT(20) NULL DEFAULT 0 COMMENT '订单下注时间',
   market_id BIGINT(20)  DEFAULT 0  COMMENT '盘口id ',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_index`(`order_no`,`bet_no` ) USING BTREE,
  KEY `idx_bet_time`(bet_time)
) ENGINE = INNODB  COMMENT '订单跳水标记表';

DROP TABLE IF EXISTS `rcs_odds_convert_mapping`;

CREATE TABLE `rcs_odds_convert_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID，自增',
  `europe` varchar(20)  DEFAULT NULL COMMENT '欧洲赔率',
  `hongkong` varchar(20)  DEFAULT NULL COMMENT '香港赔率',
  `malaysia` varchar(20)  DEFAULT NULL COMMENT '马来赔率',
  `united_kingdom` varchar(20)  DEFAULT NULL COMMENT '英赔率',
  `united_states` varchar(20)  DEFAULT NULL COMMENT '美赔率',
  `indonesia` varchar(20)  DEFAULT NULL COMMENT '印尼率',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35349 COMMENT='赔率转换映射表';


ALTER TABLE tybss_new.t_order_detail ADD INDEX idx_bet_time(bet_time);