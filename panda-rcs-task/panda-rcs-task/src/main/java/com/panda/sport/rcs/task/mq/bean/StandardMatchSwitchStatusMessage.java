package com.panda.sport.rcs.task.mq.bean;

import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class StandardMatchSwitchStatusMessage {

	/**
	 * 标准赛事id
	 */
	private Long standardMatchId;
	/**
	 * 数据源编码
	 */
	private String dataSourceCode;
	/**
	 * 状态 1 即将开赛
	 */
	private Integer oddsLive;
}
