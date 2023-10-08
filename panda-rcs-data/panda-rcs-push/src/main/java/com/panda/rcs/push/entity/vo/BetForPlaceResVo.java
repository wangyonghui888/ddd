package com.panda.rcs.push.entity.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 坑位级别货量出参 resVo
 * </p>
 *
 * @author Kir
 * @since 2021-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BetForPlaceResVo {

    /**
     * 赛事类型:1赛前,2滚球
     */
    private Integer matchType;

    /**
     * 货量-纯投注额-主队
     */
    private BigDecimal homeBetAmount;

    /**
     * 货量-纯赔付额-主队
     */
    private BigDecimal homeBetAmountPay;

    /**
     * 货量-混合型（注单亚赔大于1的，货量为注单额*亚赔；注单亚赔小于或等于1的，货量为注单额）-主队
     */
    private BigDecimal homeBetAmountComplex;

    /**
     * 货量-纯投注额-客队
     */
    private BigDecimal awayBetAmount;

    /**
     * 货量-纯赔付额-客队
     */
    private BigDecimal awayBetAmountPay;

    /**
     * 货量-混合型（注单亚赔大于1的，货量为注单额*亚赔；注单亚赔小于或等于1的，货量为注单额）-客队
     */
    private BigDecimal awayBetAmountComplex;

    /**
     * 纯投注额-平衡值
     */
    private BigDecimal betAmountEquilibriumValue;

    /**
     * 纯赔付额-平衡值
     */
    private BigDecimal betAmountPayEquilibriumValue;

    /**
     * 混合型-平衡值
     */
    private BigDecimal betAmountComplexEquilibriumValue;

    /**
     * 盘口位置
     */
    private Integer dataTypeValue;

    /**
     * forecast集合
     */
    Map<String, BigDecimal> forecastMap;
}
