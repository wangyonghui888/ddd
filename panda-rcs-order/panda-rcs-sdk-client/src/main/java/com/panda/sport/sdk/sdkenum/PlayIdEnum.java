package com.panda.sport.sdk.sdkenum;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.constants
 * @Description :
 * @Date: 2019-10-24 15:02
 */
public enum PlayIdEnum {
    ThreeWay(1),
    Halftime_ThreeWay(17),
    Handicap(4),
    Halftime_Handicap(19),
    OverUnder(2),
    Halftime_OverUnder(18),
    EvenOddTotal(15),
    BothTeamsToScore(12);
    private Integer id;
    private PlayIdEnum(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

    public Integer[] getThreeWayPlay(){
        return new Integer[]{ThreeWay.getId(),Halftime_ThreeWay.getId()};
    }
    public Integer[] getTwoWaySinglePlay(){
        return new Integer[]{EvenOddTotal.getId(),BothTeamsToScore.getId()};
    }
    public Integer[] getTwoWayDoublePlay(){
        return new Integer[]{Handicap.getId(),Halftime_Handicap.getId(),OverUnder.getId(),Halftime_OverUnder.getId()};
    }
}
