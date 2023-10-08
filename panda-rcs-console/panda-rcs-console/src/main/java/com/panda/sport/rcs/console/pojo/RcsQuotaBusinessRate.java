package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户折扣表
 *
 * @date 2022/08/15
 */
@Data
public class RcsQuotaBusinessRate implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 商户id
     */
    private Long businessId;

    /**
     * mts折扣
     */
    private BigDecimal mtsRate;
    /**
     * cts折扣
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
     * 虚拟折扣
     */
    private BigDecimal virtualRate;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}