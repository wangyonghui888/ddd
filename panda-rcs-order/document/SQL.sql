drop  table if exists rcs_predict_bet_statis;
CREATE TABLE `rcs_predict_bet_statis` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sport_id` int(20) NOT NULL COMMENT '运动种类',
  `match_id` bigint(20) NOT NULL COMMENT '标准赛事id',
  `match_type` int(11) DEFAULT NULL COMMENT '赛事类型:1赛前,2滚球',
  `play_id` int(20) DEFAULT NULL COMMENT '玩法id',
  `market_id` bigint(20) DEFAULT NULL COMMENT '盘口id',
  `odds_item` varchar(20) DEFAULT NULL COMMENT '投注项',
  `bet_score` varchar(20) DEFAULT NULL COMMENT '基准分 投注时比分',
  `bet_amount` decimal(10,2) DEFAULT NULL COMMENT '货量',
  `bet_num` bigint(20) DEFAULT NULL COMMENT '投注笔数',
  `odds_sum` decimal(10,2) DEFAULT '0.00' COMMENT '赔率和',
  `market_value_complete` varchar(20) DEFAULT NULL COMMENT '完整让球(盘口值)',
  `market_value_current` varchar(20) DEFAULT NULL COMMENT '当前让球(盘口值)',
  `play_options` varchar(255) DEFAULT NULL COMMENT '投注项名称',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_statis` (`sport_id`,`match_id`,`match_type`,`play_id`,`market_id`,`odds_item`,`bet_score`),
  key idx_market_id (market_id),
  key idx_odds_item (odds_item)
) ENGINE=InnoDB AUTO_INCREMENT=3110 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预测货量表';

drop  table if exists rcs_predict_forecast;
CREATE TABLE `rcs_predict_forecast` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sport_id` int(20) NOT NULL COMMENT '运动种类',
  `match_id` bigint(20) NOT NULL COMMENT '标准赛事id',
  `match_type` int(11) DEFAULT NULL COMMENT '赛事类型:1赛前,2滚球',
  `play_id` int(20) DEFAULT NULL COMMENT '玩法id',
  `market_id` bigint(20) DEFAULT NULL COMMENT '盘口id',
  `odds_item` varchar(20) DEFAULT NULL COMMENT '投注项',
  `bet_score` varchar(20) DEFAULT NULL COMMENT '基准分 投注时比分',
  `forecast_score` int(20) DEFAULT NULL COMMENT 'forecast预测分数(1.大小玩法时,表示两队进球和. 2.让球玩法时,表示进球数量差值)',
  `profit_amount` decimal(10,2) DEFAULT NULL COMMENT '预测盈利(庄家视角)',
  `market_value_complete` varchar(20) DEFAULT NULL COMMENT '完整让球(盘口值)',
  `market_value_current` varchar(20) DEFAULT NULL COMMENT '当前让球(盘口值)',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_forecast` (`sport_id`,`match_id`,`match_type`,`play_id`,`market_id`,`odds_item`,`bet_score`,`forecast_score`),
  key idx_market_id (market_id),
  key idx_odds_item (odds_item)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预测forecast表';

--add by lithan 2020年10月3日 11:18:07
ALTER TABLE   t_order_detail  ADD COLUMN place_num  INT COMMENT '盘口位置';

CREATE TABLE `rcs_market_num_statis` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `sport_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '体育种类',
  `match_id` bigint(20) NOT NULL COMMENT '赛事ID',
  `market_category_id` int(20) NOT NULL COMMENT '玩法ID',
  `place_num` int(11) DEFAULT NULL COMMENT '盘口位置',
  `odds_type` varchar(50) DEFAULT NULL COMMENT '投注项标识',
  `bet_order_num` decimal(20,0) NOT NULL DEFAULT '0' COMMENT '投注笔数（投注项）',
  `bet_amount` decimal(20,2) DEFAULT '0.00' COMMENT '投注量',
  `profit_value` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '盈利值',
  `standard_tournament_id` bigint(20) DEFAULT NULL COMMENT '联赛id',
  `modify_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `match_type` int(11) DEFAULT NULL COMMENT '1 早盘  2 滚球',
  `paid_amount` decimal(20,2) DEFAULT NULL COMMENT '最大派奖金额',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_num` (`match_id`,`market_category_id`,`place_num`,`odds_type`,`match_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='盘口位置统计表'