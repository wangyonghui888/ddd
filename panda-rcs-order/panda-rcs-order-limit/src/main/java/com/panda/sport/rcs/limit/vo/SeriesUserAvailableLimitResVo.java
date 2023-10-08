package com.panda.sport.rcs.limit.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 串关用户可用额度出参
 * @Author : Paca
 * @Date : 2021-12-07 20:27
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class SeriesUserAvailableLimitResVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户特殊限额类型
     */
    private String userSpecialLimitType;

    /**
     * 单日串关总赔付可用额度
     */
    private BigDecimal dailyTotalAvailableLimit;

    /**
     * 赛事单场可用额度
     */
    private BigDecimal matchSingleLimit;
    /**
     * 单日串关赔付可用额度
     */
    private Map<String, BigDecimal> availableLimitMap;
}
