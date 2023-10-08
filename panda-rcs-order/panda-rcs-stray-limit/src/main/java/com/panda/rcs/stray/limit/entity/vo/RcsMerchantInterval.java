package com.panda.rcs.stray.limit.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 高风险单注区间最高赔付金额
 */
@Data
public class RcsMerchantInterval {

    private Long id;

    private Integer sportId;

    private Integer strayType;

    private BigDecimal maxIntervalAmount;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    /**
     * 操作人IP
     */
    @TableField(exist = false)
    private String ip;

}
