package com.panda.sport.rcs.common.vo.rule;

import java.math.BigDecimal;

/**
 * 投注特征类	R23-1	投注内容-足球投注比例
 * 投注特征类	R23-2	投注内容-篮球投注比例
 * 投注特征类	R23-3	投注内容-网球投注比例
 *
 * @author lithan
 * @date 2020-07-01 09:33:13
 */
public class SportRateVo {
    //球类ID
    public Long sportId;
    //一共投注
    public Long allAmount;
    //该球类投注
    public Long sportAmount;
    //占比
    public BigDecimal rate;

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getAllAmount() {
        return allAmount;
    }

    public void setAllAmount(Long allAmount) {
        this.allAmount = allAmount;
    }

    public Long getSportAmount() {
        return sportAmount;
    }

    public void setSportAmount(Long sportAmount) {
        this.sportAmount = sportAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
