package com.panda.sport.rcs.common.vo.rule;

/**
 * 投注特征类	R22	投注玩法比例
 *
 * @author lithan
 * @date 2021-10-12 14:45:26
 */
public class PlayBetNumVo {
    /**
     * 赛种
     */
    public Long sportId;
    /**
     * 玩法ID
     */
    public Long playId;

    /**
     * 该类投注
     */
    public Long betNum;

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    public Long getBetNum() {
        return betNum;
    }

    public void setBetNum(Long betNum) {
        this.betNum = betNum;
    }
}
