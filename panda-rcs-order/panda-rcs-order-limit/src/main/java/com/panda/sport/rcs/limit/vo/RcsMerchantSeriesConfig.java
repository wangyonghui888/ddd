package com.panda.sport.rcs.limit.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 单日串关赔付总限额及单日派彩总限额
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Data
public class RcsMerchantSeriesConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 串关类型赔付总限额
     */

    private BigDecimal seriesPayoutTotalAmount;
    private Integer status;

    private Date createTime;

    private Date updateTime;

    /**
     * 操作人IP
     */
    @TableField(exist = false)
    private String ip;
}
