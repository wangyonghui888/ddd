package com.panda.sport.data.rcs.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TwowayDoubleOverLoadTriggerItem extends ThreewayOverLoadTriggerItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 限额级别
     */
    private Integer limitLevel;


    /**
     * 主二级赔率变化率
     */
    private BigDecimal homeLevelSecondOddsRate;

    /**
     * 客一级赔率变化率
     */
    private BigDecimal awayLevelFirstOddsRate;

    /**
     * 客二级赔率变化率
     */
    private BigDecimal awayLevelSecondOddsRate;


}
