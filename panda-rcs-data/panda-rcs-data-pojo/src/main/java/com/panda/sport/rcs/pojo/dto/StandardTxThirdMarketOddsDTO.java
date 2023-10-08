package com.panda.sport.rcs.pojo.dto;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.pojo.ThirdSportMarketMessage;
import lombok.Data;

import java.util.List;

/**
 * 百家赔  基础类
 */

@Data
public class StandardTxThirdMarketOddsDTO extends RcsBaseEntity<StandardTxThirdMarketOddsDTO> {
	private static final long serialVersionUID = 1L;

    /**
     * 标准比赛ID   standard_match_info.id
     */
    private String standardMatchInfoId;

    /**
     * 运动种类ID. 联赛所属体育种类id, 对应 sport.id
     */
    private Long sportId;

    /**
     * 赛事类型,0:普通赛事、1冠军赛事
     */
    private Integer matchType;

	/**
	 * 盘口投注项
	 */
	private List<ThirdSportMarketMessage> marketList;


    private List<Long> marketCategoryIds;

    /**
     * 盘口值集合
     */
    private List<String> addition1s;

    /**
     * 盘口值集合  1上游  2过期检测 3权重
     */
    private Integer channel;

}
