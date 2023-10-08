package com.panda.sport.rcs.limit.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class SingleUserAvailableLimitResVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 1-早盘，0-滚球
     */
    private Integer matchType;

    /**
     * 操盘平台
     */
    private String riskManagerCode;

    /**
     * 赛种
     */
    private Long sportId;

    /**
     * 用户特殊限额类型
     */
    private String userSpecialLimitType;

    /**
     * 单日单关总赔付可用额度
     */
    private BigDecimal dailyTotalAvailableLimit;

    /**
     * 单日单关赔付可用额度
     */
    private BigDecimal dailyAvailableLimit;

    /**
     * 赛事单场可用额度（用户单场）
     */
    private BigDecimal singleMatchAvailableLimit;

    /**
     * 玩法可用额度
     */
    private BigDecimal playAvailableLimit;

}
