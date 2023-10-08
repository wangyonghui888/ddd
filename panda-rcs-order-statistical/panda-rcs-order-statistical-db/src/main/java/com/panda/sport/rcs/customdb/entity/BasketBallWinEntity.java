package com.panda.sport.rcs.customdb.entity;


import java.math.BigDecimal;

/**
 * 篮球盈利笔数
 *
 * @author :  lithan
 * @date: 2021-12-6 19:53:07
 */
public class BasketBallWinEntity {
    //投注笔数
    private Long betNum;
    //胜利笔数
    private Long winBetNum;
    //百分比
    private BigDecimal percentage;

    public Long getBetNum() {
        return betNum;
    }

    public void setBetNum(Long betNum) {
        this.betNum = betNum;
    }

    public Long getWinBetNum() {
        return winBetNum;
    }

    public void setWinBetNum(Long winBetNum) {
        this.winBetNum = winBetNum;
    }

    public BigDecimal getPercentage() {
        try {
            return new BigDecimal(getWinBetNum()).divide(new BigDecimal(getBetNum()), 2, BigDecimal.ROUND_DOWN);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}
