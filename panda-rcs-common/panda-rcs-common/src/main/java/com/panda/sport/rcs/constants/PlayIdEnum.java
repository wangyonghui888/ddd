package com.panda.sport.rcs.constants;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.constants
 * @Description :
 * @Date: 2019-10-24 15:02
 */
public enum PlayIdEnum {
    ThreeWay(1),CornThreeWay(111),CornHalfThreeWay(119),
    HalftimeThreeWay(17),OvertimeThreeWay(126),HalftimeOvertimeThreeWay(129),

    Handicap(4),CornHandicap(113),CornHalfHandicap(121),
    HalftimeHandicap(19),OvertimeHandicap(128),HalftimeOvertimeHandicap(130),

    OverUnder(2),CornOverUnder(114),CornHalfOverUnder(122),
    HalftimeOverUnder(18),OvertimeOverUnder(127),

    HalfEvenOddTotal(15),EvenOddTotal(42),
    BothTeamsToScore(12);

    private Integer id;
    private PlayIdEnum(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

    public Integer[] getThreeWayPlay(){
        return new Integer[]{ThreeWay.getId(),HalftimeThreeWay.getId(),
                CornThreeWay.getId(),CornHalfThreeWay.getId(),OvertimeThreeWay.getId(),
                HalftimeOvertimeThreeWay.getId()};
    }
    public Integer[] getTwoWaySinglePlay(){
        return new Integer[]{EvenOddTotal.getId(),BothTeamsToScore.getId(),HalfEvenOddTotal.getId(),};
    }
    public Integer[] getTwoWayDoublePlay(){
        return new Integer[]{Handicap.getId(),HalftimeHandicap.getId(),
        		CornHandicap.getId(),CornHalfHandicap.getId(),
                OvertimeHandicap.getId(),HalftimeOvertimeHandicap.getId(),
                OverUnder.getId(),HalftimeOverUnder.getId(),
                CornOverUnder.getId(),CornHalfOverUnder.getId(),
                OvertimeOverUnder.getId()
        };
    }

    public Integer[] getOverUnderPlay(){
        return new Integer[]{OverUnder.getId(),CornOverUnder.getId(),OvertimeOverUnder.getId(),
                CornHalfOverUnder.getId(),HalftimeOverUnder.getId()};
    }
}
