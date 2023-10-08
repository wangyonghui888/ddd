package com.panda.sport.rcs.mgr.enums;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.enums
 * @Description :  TODO
 * @Date: 2020-09-06 11:33
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum RcsQuotaUserSingleSiteQuotaEnum {
    NOTHING(-1,"0.05","0.05"),
    ONE(1,"0.2","0.3"),
    TWO(2,"0.1","0.2"),
    THREE(3,"0.1","0.2"),
    FOUR(4,"0.1","0.1"),
    FIVE(5,"0.1","0.1"),
    SIX(6,"0.1","0.1"),
    SEVEN(7,"0.1","0.1"),
    EIGHT(8,"0.1","0.1"),
    NINE(9,"0.1","0.1"),
    TEN(10,"0.05","0.05"),
    ELEVEN(11,"0.05","0.05"),
    TWELVE(12,"0.05","0.05"),
    THIRTEEN(13,"0.05","0.05"),
    FOURTEEN(14,"0.05","0.05"),
    FIFTEEN(15,"0.05","0.05"),
    SIXTEEN(16,"0.05","0.05"),
    SEVENTEEN(17,"0.05","0.05"),
    EIGHTEEN(18,"0.05","0.05"),
    NINETEEN(19,"0.05","0.05"),
    TWENTY(20,"0.05","0.05")
    ;
    /**
     * 联赛等级
     */
    private Integer level;
    /**
     * 早盘比例
     */
    private String earlyTradingRatio;
    /**
     * 滚球比例
     */
    private String rollingRatio;

    private RcsQuotaUserSingleSiteQuotaEnum(Integer level, String earlyTradingRatio, String rollingRatio) {
        this.level = level;
        this.earlyTradingRatio = earlyTradingRatio;
        this.rollingRatio = rollingRatio;
    }

    public Integer getLevel() {
        return level;
    }

    public String getEarlyTradingRatio() {
        return earlyTradingRatio;
    }

    public String getRollingRatio() {
        return rollingRatio;
    }
}
