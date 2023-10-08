package com.panda.sport.rcs.pojo;

import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class StandardMatchSwitchStatusMessage{

	/**
	 * 标准赛事id
	 */
	private Long standardMatchId;
	/**
	 * 数据源编码
	 */
	private String dataSourceCode;
	/**
	 * 状态  0 滚球标识切换为赛前标识  1 即将开赛
	 */
	private Integer oddsLive;


	private Integer matchStatus;
}
