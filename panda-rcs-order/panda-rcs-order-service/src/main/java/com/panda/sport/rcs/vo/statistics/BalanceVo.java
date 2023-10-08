package com.panda.sport.rcs.vo.statistics;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.vo.statistics
 * @Description :  TODO
 * @Date: 2019-11-11 18:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceVo extends RcsBaseEntity<BalanceVo> {

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
    private Long marketCategoryId;
    /**
     * 玩法ID
     */
    private String subPlayId;

    /**
     * 盘口位置
     */
    private Integer placeNum;

    /**
     * 位置ID
     */
    private String placeNumId;

    /**
     * 盘口ID
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long marketId;
    /**
     * 盘口是否关联 1关联
     */
    private Integer relevanceType;
    /**
     * @Description 主队水差
     * @Param
     * @Author Sean
     * @Date 15:36 2020/1/11
     * @return
     **/
    private Integer changeRate;
    /**
     * 主队投注额
     */
    private Long homeAmount;
    /**
     * 客队投注额
     */
    private Long awayAmount;
    /**
     * 和投注额
     */
    private Long tieAmount;
    /**
     * 水差差值
     */
    private BigDecimal waterValue;
    /**
     * 改变水差状态
     */
    private Integer changeRateStatus;
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

    public BalanceVo(Long sportId, Long matchId, Long playId, Integer placeNum, Long marketId,String subPlayId) {
        this.sportId = sportId;
        this.matchId = matchId;
        this.marketCategoryId = playId;
        this.placeNum = placeNum;
        this.subPlayId = subPlayId;
        if (RcsConstant.BASKETBALL_X_EU_PLAYS.contains(playId.intValue()) ||
                RcsConstant.BASKETBALL_X_MY_PLAYS.contains(playId.intValue()) ||
                (!(SportIdEnum.isFootball(sportId.intValue()) || SportIdEnum.isBasketball(sportId)))){
            this.placeNumId = matchId + "_" + playId + "_" + subPlayId + "_" + placeNum;
        }else {
            this.placeNumId = matchId + "_" + playId + "_" + placeNum;
        }
//        this.placeNumId = matchId + "_" + playId + "_" + placeNum;
        this.marketId = marketId;
    }

    public BalanceVo(Long matchId, Long marketCategoryId, Long marketId, Long balanceValue, Integer relevanceType) {
        this.matchId = matchId;
        this.marketCategoryId = marketCategoryId;
        this.marketId = marketId;
        this.balanceValue = balanceValue;
        this.relevanceType = relevanceType;
    }

    public BalanceVo(Long matchId, Long marketCategoryId, Long marketId, BigDecimal waterValue) {
        this.matchId = matchId;
        this.marketCategoryId = marketCategoryId;
        this.marketId = marketId;
        this.waterValue = waterValue;
    }

    public BalanceVo(Long matchId, Long marketCategoryId, Long marketId, Integer changeRate) {
        this.matchId = matchId;
        this.marketCategoryId = marketCategoryId;
        this.marketId = marketId;
        this.changeRate = changeRate;
    }

    //同时减去最小的
    public BalanceVo(Long matchId, Long marketId, Long balanceValue, Long homeAmount, Long awayAmount, Long tieAmount) {
        this.matchId = matchId;
        this.marketId = marketId;
        this.balanceValue = balanceValue;
        this.homeAmount = homeAmount;
        this.awayAmount = awayAmount;
        this.tieAmount = tieAmount;
    }

    public String generateKey() {
        return String.format("jumpBalance_%s_%s_risk", getMatchId(), getMarketCategoryId());
    }
}
