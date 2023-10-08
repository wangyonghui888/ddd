package com.panda.sport.rcs.limit.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 单日串关赛种赔付限额及派彩限额
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Data
public class RcsMerchantSportLimit {
    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 赛种ID
     */
    private Long sportId;

    private BigDecimal strayLimitAmount;

    private Integer status;

    private Date createTime;

    private Date updateTime;


}
