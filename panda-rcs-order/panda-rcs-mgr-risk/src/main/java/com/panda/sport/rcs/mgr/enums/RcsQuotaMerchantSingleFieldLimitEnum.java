package com.panda.sport.rcs.mgr.enums;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.enums
 * @Description :  TODO
 * @Date: 2020-09-05 10:27
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum RcsQuotaMerchantSingleFieldLimitEnum {
    NOTHING(-1,"0.4","0.4"),
    ONE(1,"1","1"),
    TWO(2,"1","1"),
    THREE(3,"0.8","0.8"),
    FOUR(4,"0.8","0.8"),
    FIVE(5,"0.6","0.6"),
    SIX(6,"0.4","0.4"),
    SEVEN(7,"0.4","0.4"),
    EIGHT(8,"0.4","0.4"),
    NINE(9,"0.4","0.4"),
    TEN(10,"0.4","0.4"),
    ELEVEN(11,"0.4","0.4"),
    TWELVE(12,"0.4","0.4"),
    THIRTEEN(13,"0.4","0.4"),
    FOURTEEN(14,"0.4","0.4"),
    FIFTEEN(15,"0.4","0.4"),
    SIXTEEN(16,"0.4","0.4"),
    SEVENTEEN(17,"0.4","0.4"),
    EIGHTEEN(18,"0.4","0.4"),
    NINETEEN(19,"0.4","0.4"),
    TWENTY(20,"0.4","0.4")
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

    private RcsQuotaMerchantSingleFieldLimitEnum(Integer level, String earlyTradingRatio, String rollingRatio) {
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
