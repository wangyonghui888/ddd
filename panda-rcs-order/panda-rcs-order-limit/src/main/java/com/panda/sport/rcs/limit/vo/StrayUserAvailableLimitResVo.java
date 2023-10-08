package com.panda.sport.rcs.limit.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 单关用户可用额度出参
 * @Author : Paca
 * @Date : 2021-12-17 11:57
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StrayUserAvailableLimitResVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 单日串关总赔付可用额度
     */
    private BigDecimal dailyTotalAvailableLimit;
    /**
     * 用户特殊限额类型
     */
    private String userSpecialLimitType;
    /**
     * 用户串关类型可用额度
     */
    private List<StrayUserSeriesLimitResVo> strayUserSeriesLimitResVoList;
    /**
     * 用户串关赛种可用额度
     */
    private List<StrayUserSportLimitResVo> strayUserSportLimitResVoList;
    /**
     * 赛事单场可用额度
     */
    private BigDecimal matchSingleLimit;
}
