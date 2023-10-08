package com.panda.sport.rcs.mongo;

import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "rcs_market_category")
public class MarketCategory {
	
	/**
	 * @Description //标准玩法id
	 **/
	@Field(value = "id")
	private Long id;

	private String type;
	/**
	 * 玩法名称
	 */
	private I18nBean names;
	/**
	 * 赛事Id
	 */
	private String matchId;
	/**
	 *比赛开始时间
	 */
	private String matchStartTime;
	/**
	 * 体育种类
	 */
	private Long sportId;
	/**
	 * 玩法阶段类型
	 */
	private Integer playPhaseType;

	/**
	 * 玩法级别状态 数据源
	 */
	private Integer playTradeType;
	
	private String crtTime;
	/**
	 * 盘口集合
	 */
	private List<MatchMarketLiveOddsVo.MatchMarketVo> matchMarketVoList;
	
}
