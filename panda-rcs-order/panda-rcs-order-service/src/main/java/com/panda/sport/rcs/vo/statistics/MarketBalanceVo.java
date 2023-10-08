package com.panda.sport.rcs.vo.statistics;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author holly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketBalanceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 赛种ID
     */
    private Long sportId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Long playId;

    /**
     * 盘口位置
     */
    private Integer placeNum;

    /**
     * 位置ID
     */
    private String placeNumId;
    /**
     * 玩法ID
     */
    private String subPlayId;

    /**
     * 盘口ID
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long marketId;
    /**
     * 盘口值
     */
    private String marketValue;
    /**
     * 主队投注额
     */
    private Long homeAmount = 0L;
    /**
     * 客队投注额
     */
    private Long awayAmount = 0L;
    /**
     * 和投注额
     */
    private Long tieAmount = 0L;
    /**
     * 主队margin
     */
    private BigDecimal homeMargin;
    /**
     * 客队margin
     */
    private BigDecimal awayMargin;
    /**
     * 平局margin
     */
    private BigDecimal tieMargin;
    /**
     * 水差
     */
    private String awayAutoChangeRate;

    /**
     * 平衡值
     */
    private Long balanceValue;
    /**
     * 当前投注方 1 主 2 客 * 平
     */
    private String currentSide;
    /**
     * 跳盘平衡值
     */
    private Long jumpMarketBalance;
    /**
     * 跳盘平衡值所在投注项
     */
    private String jumpMarketOddsType;

    public MarketBalanceVo(Long sportId, Long matchId, Long playId, Integer placeNum, Long marketId,String subPlayId) {
        this.sportId = sportId;
        this.matchId = matchId;
        this.playId = playId;
        this.placeNum = placeNum;
        if (RcsConstant.BASKETBALL_X_EU_PLAYS.contains(playId.intValue()) ||
                RcsConstant.BASKETBALL_X_MY_PLAYS.contains(playId.intValue()) ||
                RcsConstant.OTHER_CAN_TRADE_SPORT.contains(sportId.intValue())){
            this.placeNumId = matchId + "_" + playId + "_" + subPlayId + "_" + placeNum;
        }else {
            this.placeNumId = matchId + "_" + playId + "_" + placeNum;
        }
        this.marketId = marketId;
    }
}
