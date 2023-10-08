package com.panda.sport.data.rcs.dto.limit;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.rcs.dto.limit
 * @Description : 投注额限制
 * @Author : Paca
 * @Date : 2020-09-25 16:49
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@NoArgsConstructor
public class BetAmountLimitVo implements Serializable {

    private static final long serialVersionUID = -4327740843038755909L;

    /**
     * 单关单注最低投注额
     */
    private BigDecimal singleMinBet;

    /**
     * 串关单注最低投注额
     */
    private BigDecimal seriesMinBet;

    /**
     * 占单关投注限额的比例
     */
    private BigDecimal seriesMaxBetRatio;

    public BetAmountLimitVo(BigDecimal defaultValue) {
        this.singleMinBet = defaultValue;
        this.seriesMinBet = defaultValue;
        this.seriesMaxBetRatio = defaultValue;
    }

}
