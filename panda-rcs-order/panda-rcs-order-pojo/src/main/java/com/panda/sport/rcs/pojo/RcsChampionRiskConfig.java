package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.log.annotion.format.LogFormatAnnotion;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Kir
 * @since 2021-06-08
 */
@Data
public class RcsChampionRiskConfig extends RcsBaseEntity<RcsChampionRiskConfig> {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法id
     */
    private Long playId;

    /**
     * 盘口id
     */
    private Long marketId;

    /**
     * 单项累计跳水注额
     */
    private BigDecimal oneTotalOddsAmount;

    /**
     * 单枪跳水注额
     */
    private BigDecimal oneOddsAmount;

    /**
     * 一次跳水概率
     */
    private BigDecimal oneProbability;

    /**
     * 二次跳水概率
     */
    private BigDecimal twoProbability;

    /**
     * 三次跳水概率
     */
    private BigDecimal threeProbability;

    public RcsChampionRiskConfig(BigDecimal oneOddsAmount,BigDecimal oneTotalOddsAmount,BigDecimal oneProbability,BigDecimal twoProbability,BigDecimal threeProbability){
        this.oneOddsAmount = oneOddsAmount;
        this.oneTotalOddsAmount = oneTotalOddsAmount;
        this.oneProbability = oneProbability;
        this.twoProbability = twoProbability;
        this.threeProbability = threeProbability;
    };
    public RcsChampionRiskConfig(){

    };
}
