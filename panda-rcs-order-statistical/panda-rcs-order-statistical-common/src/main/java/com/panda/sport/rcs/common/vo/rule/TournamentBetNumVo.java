package com.panda.sport.rcs.common.vo.rule;

import java.math.BigDecimal;

/**
 * 投注特征类	R17	投注联赛比例
 *
 * @author lithan
 * @date 2020-07-08 11:54:50
 */
public class TournamentBetNumVo {
    /**
     * 联赛ID
     */
    public Long tournamentId;
    /**
     * 该类投注
     */
    public Long betNum;

    public String tournamentName;

    public String tournamentName1;

    public String tournamentName2;

    public String tournamentName3;


    public String getTournamentName() {
        String tournamentName = getTournamentName1() + getTournamentName2() + getTournamentName3();
        tournamentName = tournamentName.replace("&nbsp;", " ");
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getTournamentName1() {
        if (tournamentName1 == null) {
            return "";
        }
        return tournamentName1;
    }

    public void setTournamentName1(String tournamentName1) {
        this.tournamentName1 = tournamentName1;
    }

    public String getTournamentName2() {
        if (tournamentName2 == null) {
            return "";
        }
        return tournamentName2;
    }

    public void setTournamentName2(String tournamentName2) {
        this.tournamentName2 = tournamentName2;
    }

    public String getTournamentName3() {
        if (tournamentName3 == null) {
            return "";
        }
        return tournamentName3;
    }

    public void setTournamentName3(String tournamentName3) {
        this.tournamentName3 = tournamentName3;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getBetNum() {
        return betNum;
    }

    public void setBetNum(Long betNum) {
        this.betNum = betNum;
    }
}
