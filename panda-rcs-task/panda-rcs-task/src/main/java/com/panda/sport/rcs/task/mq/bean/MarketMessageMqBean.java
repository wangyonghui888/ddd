package com.panda.sport.rcs.task.mq.bean;

import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MarketMessageMqBean implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * 标准比赛ID   standard_match_info.id
     */
    private Long standardMatchInfoId;


    private List<MatchMarketLiveOddsVo.MatchMarketVo> matchMarketVos;

}
