package com.panda.sport.rcs.common.vo;

import java.io.Serializable;
import java.util.List;

public class MatchLimitDataReqVo implements Serializable {

    private static final long serialVersionUID = 4421884046177425425L;
    private Integer sportId;
    private Integer tournamentLevel;
    private Long matchId;

    /**
     * 1早盘  2滚球
     */
    public Long matchType;

    public Long getMatchType() {
        return matchType;
    }

    public void setMatchType(Long matchType) {
        this.matchType = matchType;
    }

    private List<Integer> dataTypeList;

    public MatchLimitDataReqVo() {
    }

    public Integer getSportId() {
        return this.sportId;
    }

    public Integer getTournamentLevel() {
        return this.tournamentLevel;
    }

    public Long getMatchId() {
        return this.matchId;
    }

    public List<Integer> getDataTypeList() {
        return this.dataTypeList;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public void setDataTypeList(List<Integer> dataTypeList) {
        this.dataTypeList = dataTypeList;
    }


    protected boolean canEqual(Object other) {
        return other instanceof MatchLimitDataReqVo;
    }

}
