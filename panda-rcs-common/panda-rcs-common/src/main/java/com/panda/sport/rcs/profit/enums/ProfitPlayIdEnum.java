package com.panda.sport.rcs.profit.enums;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation
 * @Description :  期望值玩法Id
 * @Date: 2019-12-23 10:21
 * @ModificationHistory Who    When    What单双
 * --------  ---------  --------------------------
 */
public enum ProfitPlayIdEnum {
    /** 全场让球**/
    Handicap(4,"全场让球"),
    /** 半场让球**/
    Halftime_Handicap(19,"半场让球"),
    /** 全场大小盘**/
    OverUnder(2,"全场大小盘"),
    /** 半场大小球**/
    Halftime_OverUnder(18,"半场大小球"),

    /**  双方是否都进球 **/
    BothTeamsToScore(12,"双方是否都进球"),
    /**  全场单双 **/
    OddEven(15,"全场单双"),
    /** 角球让球盘**/
    Corners_Handicap(113,"角球让球盘"),
    /** 角球大小盘 **/
    Corners_Total(114,"角球大小盘"),


    Corners_Halftime_Handicap(121,"角球半场让球盘"),
    Corners_Halftime_Total(122,"角球半场大小盘"),

    /** 哪个球队获得角球多**/
    Corner_1x2(111,"哪个球队获得角球多"),
    /** 角球总数区间**/
    Corner_Range(117,"角球总数区间"),
    /**  角球单/双**/
    Corners_OddEven(118,"角球单/双"),

    /**  角球上半场独赢**/
    Corner_alftime_1x2(119,"上半场哪个球队获得角球多");







    private Integer code;
    private String value;

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    ProfitPlayIdEnum(Integer code, String value) {
        this.code = code;
        this.value=value;
    }
}
