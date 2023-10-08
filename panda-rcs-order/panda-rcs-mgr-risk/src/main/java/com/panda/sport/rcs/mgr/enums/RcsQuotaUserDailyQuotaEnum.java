package com.panda.sport.rcs.mgr.enums;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2020-11-25 11:27
 **/
public enum RcsQuotaUserDailyQuotaEnum {
    ALL(0,"1.5","0.6"),
    FOOTBALL(1,"1","0.5"),
    BASKETBALL(2,"1","0.5"),
    TENNIS(5,"0.5","0.3"),
    TABLE_TENNIS(8,"0.5","0.3"),
    BADMINTON(10,"0.5","0.3"),
    SL1CK(7,"0.5","0.3"),
    VOLLEYBALL(9,"0.5","0.3"),
    ICE_HOCKEY(4,"0.5","0.3"),
    BASEBALL(3,"0.5","0.3"),
    AMERICAN_FOOTBALL(6,"0.5","0.3"),
    OTHER(-1,"0.5","0.3");

    private RcsQuotaUserDailyQuotaEnum(Integer sportId, String dayCompensationProportion, String crossDayCompensationProportion) {
        this.sportId = sportId;
        this.dayCompensationProportion = dayCompensationProportion;
        this.crossDayCompensationProportion = crossDayCompensationProportion;
    }

    public Integer getSportId() {
        return sportId;
    }

    public String getDayCompensationProportion() {
        return dayCompensationProportion;
    }

    public String getCrossDayCompensationProportion() {
        return crossDayCompensationProportion;
    }

    /**
     * 体育种类
     */
    private Integer sportId;
    /**
     *单日赔付
     */
    private String dayCompensationProportion;
    /**
     * 单日串关赔付
     */
    private String crossDayCompensationProportion;
}
