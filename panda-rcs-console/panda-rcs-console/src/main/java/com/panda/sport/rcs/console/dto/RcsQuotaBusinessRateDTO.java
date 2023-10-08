package com.panda.sport.rcs.console.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RcsQuotaBusinessRateDTO {

    /**
     * 商户id
     */
    private Long businessId;

    /**
     * 商户code
     */
    private String businessCode;

    /**
     * mts折扣
     */
    private BigDecimal mtsRate;

    /**
     * cts折扣利率
     */
    private BigDecimal ctsRate;

    /**
     * gts折扣
     */
    private BigDecimal gtsRate;

    /**
     * ots折扣
     */
    private BigDecimal otsRate;

    /**
     * rts折扣
     */
    private BigDecimal rtsRate;

    /**
     * mts折扣通用
     */
    private BigDecimal mtsRateAll;

    /**
     * cts折扣通用
     */
    private BigDecimal ctsRateAll;

    /**
     * gts折扣通用
     */
    private BigDecimal gtsRateAll;

    /**
     * ots折扣通用
     */
    private BigDecimal otsRateAll;

    /**
     * rts折扣通用
     */
    private BigDecimal rtsRateAll;

    /**
     * 虚拟折扣
     */
    private BigDecimal virtualRate;

    /**
     * 虚拟折扣通用
     */
    private BigDecimal virtualRateAll;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 商户id集合
     */
    private List<String> businessIds;

    private String busIds;
}
