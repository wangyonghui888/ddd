package com.panda.sport.rcs.vo.statistics;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

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
@AllArgsConstructor
public class BalanceVo extends RcsBaseEntity<BalanceVo> {
    /**
     * @Description 赛事id
     * @Param
     **/
    private Long matchId;
    /**
     * @Description 玩法id
     * @Param
     **/
    private Long marketCategoryId;
    /**
     * @Description 盘口id
     * @Param
     **/
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long marketId;
    /**
     * @Description 平衡值
     * @Param
     **/
    private Long balanceValue;
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
}
