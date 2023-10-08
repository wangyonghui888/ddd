package com.panda.sport.rcs.pojo.vo;

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
     * 虚拟折扣
     */
    private BigDecimal virtualRate;

    /**
     * VR藏单状态(1--关闭 2--开启)
     */
    private Integer vrEnable;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}