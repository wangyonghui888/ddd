package com.panda.sport.rcs.limit.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 商户每日限额预警出参
 * @Author : Paca
 * @Date : 2021-11-27 15:03
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MerchantDailyLimitResVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户ID
     */
    private String businessId;

    /**
     * 商户名称
     */
    private String businessName;

    /**
     * 单日限额
     */
    private Long dailyLimit;

    /**
     * 单日已用限额
     */
    private BigDecimal businessUsedLimit;

    /**
     * 已用额度百分比
     */
    private BigDecimal percentageOfUsedQuota;
    
    /**
     * 	单日串关限额
     */
    private Long dailySeriesLimit;
    
    /**
     * 	单日串关已用限额
     */
    private BigDecimal businessSeriesUsedLimit;
    
    /**
     * 	已用串关额度百分比
     */
    private BigDecimal percentageSeriesOfUsedQuota;
}
