package com.panda.sport.rcs.common.vo.rule;

/**
 * 投注特征类	R24	投注场次
 *
 * @author lithan
 * @date 2021-10-12 14:45:26
 */
public class MatchBetNumVo {

    /**
     * 赛事ID
     */
    public Long matchId;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getBetNum() {
        return betNum;
    }

    public void setBetNum(Long betNum) {
        this.betNum = betNum;
    }

    /**
     * 该类投注
     */
    public Long betNum;

}
