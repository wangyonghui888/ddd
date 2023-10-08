package com.panda.sport.rcs.common.vo.rule;

/**
 * 投注特征类	R23	用户单场盈利程度
 *
 * @author lithan
 * @date 2021-10-12 14:45:26
 */
public class MatchProfitNumVo {

    /**
     * 赛种
     */
    public Long sportId;

    /**
     * 联赛
     */
    public Long tournamentLevel;

    /**
     * 赛事
     */
    public Long matchId;

    /**
     * 1早盘  2滚球
     */
    public Integer matchType;


    /**
     * 盈利金额
     */
    public Long profitAmount;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(Long profitAmount) {
        this.profitAmount = profitAmount;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Long tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public Integer getMatchType() {
        return matchType;
    }

    public void setMatchType(Integer matchType) {
        this.matchType = matchType;
    }
}
