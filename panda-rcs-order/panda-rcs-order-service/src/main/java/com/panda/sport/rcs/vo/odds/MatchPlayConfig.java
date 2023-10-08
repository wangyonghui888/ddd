package com.panda.sport.rcs.vo.odds;

import com.panda.sport.rcs.pojo.RcsMatchPlayConfig;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MatchPlayConfig {

    private String playId;

    /**
     * 赔率类型
     * MY 马来  ， EU：欧赔
     */
    private String marketType;

    /**
     * 盘口差
     */
    private String marketHeadGap;
//    /**
//     * 玩法配置的水差
//     */
//    private RcsMatchPlayConfig playWaterConfig;
    /**
     * 水差
     */
    private BigDecimal waterDiff;

    /**
     * 最大盘口数
     */
//	private Integer marketCount;

    /**
     * 位置配置
     */
    private List<MatchMarketPlaceConfig> placeConfig;

    /**
     * 当前玩法对应的盘口赔率
     */
    private List<RcsStandardMarketDTO> marketList;

    /**
     * 玩法级别开关封锁
     */
    private Integer status;

    /**
     * 玩法级别操盘状态切换
     */
    private Integer tradeStatus;

    /**
     * 是否自动关盘，0-否，1-是
     */
    private Integer autoCloseFlag;

    /**
     * 分时模板配置
     */
    private RcsTournamentTemplatePlayMargain rcsTournamentTemplatePlayMargain;

    public Integer getAutoCloseFlag() {
        return autoCloseFlag == null ? 0 : autoCloseFlag;
    }

	public MatchPlayConfig(String playId, String marketType ,List<MatchMarketPlaceConfig> placeConfig) {
		super();
		this.playId = playId;
		this.marketType = marketType;
		this.placeConfig = placeConfig;
	}
	public MatchPlayConfig(String playId, String marketType ,List<MatchMarketPlaceConfig> placeConfig,String marketHeadGap,RcsTournamentTemplatePlayMargain rcsTournamentTemplatePlayMargain) {
		super();
		this.playId = playId;
		this.marketType = marketType;
		this.placeConfig = placeConfig;
		this.marketHeadGap = marketHeadGap;
		this.rcsTournamentTemplatePlayMargain = rcsTournamentTemplatePlayMargain;
	}

	public MatchPlayConfig() {
		super();
	}

	public MatchPlayConfig(String playId, String marketType) {
		this.playId = playId;
		this.marketType = marketType;
	}
}
