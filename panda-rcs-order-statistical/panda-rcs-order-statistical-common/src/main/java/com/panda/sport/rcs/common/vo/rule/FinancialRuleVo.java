package com.panda.sport.rcs.common.vo.rule;

import java.math.BigDecimal;

/**
 * 财务特征类 规则 R1	用户盈亏金额
 *
 * @author lithan
 * @date 2020-06-28 13:24:45
 */
public class FinancialRuleVo {
    /**
     * 投注金额
     */
    public BigDecimal betAmount;

    /**
     * 盈利金额(包括本金)
     */
    public BigDecimal profitAmount;

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }
}
