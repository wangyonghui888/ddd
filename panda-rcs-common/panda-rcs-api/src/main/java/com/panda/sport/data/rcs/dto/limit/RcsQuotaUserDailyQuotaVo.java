package com.panda.sport.data.rcs.dto.limit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户单日限额
 *
 * @author : lithan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RcsQuotaUserDailyQuotaVo implements Serializable {

    private static final long serialVersionUID = 7932964012007744423L;

    /**
     * 体育种类   -1总值
     */
    private Integer sportId;

    /**
     * 单日赔付
     */
    private BigDecimal dayCompensation;

    /**
     * 串关单日赔付
     */
    private BigDecimal crossDayCompensation;

}
