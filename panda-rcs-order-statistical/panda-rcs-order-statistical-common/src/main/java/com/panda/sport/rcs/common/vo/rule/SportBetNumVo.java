package com.panda.sport.rcs.common.vo.rule;

/**
 * 投注特征类	R16	投注赛种比例 vo
 *
 * @author lithan
 * @date 2020-07-08 13:46:00
 */
public class SportBetNumVo {
    /**
     * 赛种id
     */
    public Long sportId;
    /**
     * 该类投注笔数
     */
    public Long betNum;

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getBetNum() {
        return betNum;
    }

    public void setBetNum(Long betNum) {
        this.betNum = betNum;
    }
}
