package com.panda.sport.rcs.limit.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 商户可用额度出参
 * @Author : Paca
 * @Date : 2021-12-05 19:05
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MerchantAvailableLimitResVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户单日可用额度
     */
    private BigDecimal merchantDailyAvailableLimit;
    
    /**
     * 	商户单日串关可用额度
     */
    private BigDecimal merchantDailySeriesAvailableLimit;

    /**
     * 1-早盘，0-滚球
     */
    private Integer matchType;

    /**
     * 操盘平台
     */
    private String riskManagerCode;

    /**
     * 商户单场初始额度
     */
    private BigDecimal merchantMatchInitLimit;

    /**
     * 商户单场可用额度
     */
    private BigDecimal merchantMatchAvailableLimit;
}
