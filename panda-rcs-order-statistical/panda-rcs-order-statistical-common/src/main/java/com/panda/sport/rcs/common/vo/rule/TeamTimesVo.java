package com.panda.sport.rcs.common.vo.rule;

/**
 * 投注内容-球队次数
 *
 * @author lithan
 * @date 2020-07-01
 */
public class TeamTimesVo {
    //球队ID
    public Long teamId;
    //次数
    public Long times;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getTimes() {
        return times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }
}

