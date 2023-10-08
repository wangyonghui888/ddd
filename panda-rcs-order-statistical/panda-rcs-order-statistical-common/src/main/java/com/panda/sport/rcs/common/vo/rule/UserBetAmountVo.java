package com.panda.sport.rcs.common.vo.rule;

import java.math.BigDecimal;

/**
 * R4	投注金额标准 vo
 *
 * @author lithan
 * @date 2020-07-08 13:32:40
 */
public class UserBetAmountVo {
    /**
     * 投注金额
     */
    public BigDecimal betAmount;

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }
}

