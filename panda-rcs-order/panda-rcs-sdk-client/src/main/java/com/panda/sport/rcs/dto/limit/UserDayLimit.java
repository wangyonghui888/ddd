package com.panda.sport.rcs.dto.limit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.rcs.dto.limit
 * @Description : 用户单日限额
 * @Author : Paca
 * @Date : 2020-10-11 20:50
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDayLimit implements Serializable {

    private static final long serialVersionUID = -3320336859400947231L;

    /**
     * 赛种
     */
    private Integer sportId;

    /**
     * 单日赔付限额
     */
    private BigDecimal dayCompensation;

    /**
     * 单日串关赔付限额
     */
    private BigDecimal crossDayCompensation;

    /**
     * 单日赔付总限额
     */
    private BigDecimal dayCompensationTotal;

    /**
     * 单日串关赔付总限额
     */
    private BigDecimal crossDayCompensationTotal;

    public UserDayLimit(BigDecimal defaultValue) {
        this.dayCompensation = defaultValue;
        this.crossDayCompensation = defaultValue;
        this.dayCompensationTotal = defaultValue;
        this.crossDayCompensationTotal = defaultValue;
    }

}
