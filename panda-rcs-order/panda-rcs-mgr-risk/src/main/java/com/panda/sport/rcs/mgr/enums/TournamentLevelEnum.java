package com.panda.sport.rcs.mgr.enums;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.enums
 * @Description :  TODO
 * @Date: 2020-09-12 15:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum TournamentLevelEnum {
    ONE(1,"0.4"),
    TWO(2,"0.4"),
    THREE(3,"0.4"),
    FOUR(4,"0.4"),
    FIVE(5,"0.4"),
    SIX(6,"0.4"),
    SEVEN(7,"0.4"),
    EIGHT(8,"0.4"),
    NINE(9,"0.4"),
    TEN(10,"0.4"),
    ELEVEN(11,"0.4"),
    OTHER(-1,"0.4");
    /**
     * 联赛等级
     */
    private Integer tournamentLevel;
    /**
     * 限额比例
     */
    private String quotaProportion;

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public String getQuotaProportion() {
        return quotaProportion;
    }

    public void setQuotaProportion(String quotaProportion) {
        this.quotaProportion = quotaProportion;
    }

    private TournamentLevelEnum(Integer tournamentLevel, String quotaProportion) {
        this.tournamentLevel = tournamentLevel;
        this.quotaProportion = quotaProportion;
    }
}
