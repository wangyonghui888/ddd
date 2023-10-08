package com.panda.sport.rcs.vo.odds;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MatchOddsConfig {

    private String linkId;

    private String matchId;

    /**
     * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
     */
    private Integer matchType;

    /**
     * 玩法配置
     */
    private List<MatchPlayConfig> playConfigList;

    /**
     * 赛事级别开关封锁
     */
    private Integer status;

    /**
     * 赛事级别操盘状态切换
     */
    private Integer tradeStatus;
    /**
     * 用户id
     */
    private Integer userId;
    
	public MatchOddsConfig() {
		super();
	}


	public MatchOddsConfig(String matchId, Integer matchType) {
		super();
		this.matchId = matchId;
		this.matchType = matchType;
		this.playConfigList = new ArrayList<MatchPlayConfig>();
	}

}
