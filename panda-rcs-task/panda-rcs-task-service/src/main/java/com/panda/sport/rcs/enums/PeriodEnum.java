package com.panda.sport.rcs.enums;

/**
 * 阶段枚举
 * @author :  enzo
 */
public enum PeriodEnum {
    FULL_TIME_1(1, 1,0,10001L, "常规进球"),
    CORNER_KICK_1(1, 2,0,10002L, "常规角球"),
    EXTRA_TIME_1(1, 4,0,10003L, "加时进球"),
    PENALTY_SHOOT_1(1, 5,0,10004L, "点球大战"),
    PENALTY_CARD_1(1, 3,0,10005L, "常规罚牌"),

    FULL_TIME_2(2, 7,0,20001L, "全场"),
    FIRST_HALF_2(2, 3,1, 20002L,"上半场"),
    SECTION_ONE_2(2,1 ,13, 20003L,"第一节"),
    SECTION_TWO_2(2, 2,14, 20004L,"第二节"),
    SECTION_THREE_2(2, 4,15, 20005L,"第三节"),
    SECTION_FOUR_2(2, 5,16, 20006L,"第四节"),
    SECOND_HALF_2(2, 6,2, 20007L,"下半场"),


    SET_ONE_5(5, 1,8,0L, "第一盘"),
    SET_TWO_5(5, 2,9,0L, "第二盘"),
    SET_THREE_5(5, 3,10,0L, "第三盘"),
    SET_FOUR_5(5, 4,11,0L, "第四盘"),
    SET_FIVE_5(5, 5,12,0L, "第五盘"),


    ICE_HOCKEY_4(4, 1, 40, 0L,  "加时赛"),

    VOLLEYBALL_THREE_9(9, 3,10,0L, "第三局"),
    VOLLEYBALL_FOUR_9(9, 4,11,0L, "第四局"),
    VOLLEYBALL_FIVE_9(9, 5,12,0L, "第五局"),
    VOLLEYBALL_SIX_9(9, 6,441,0L, "第六局"),
    VOLLEYBALL_SEVEN_9(9,7, 442,0l, "第七局"),
    ;

    private int sportId;
    private int sort;
    private int period;
    private Long categorySetId;
    private String name;

    PeriodEnum(int sportId, int sort, int period, Long categorySetId, String name) {
        this.sportId = sportId;
        this.sort = sort;
        this.period = period;
        this.categorySetId = categorySetId;
        this.name = name;
    }

    public int getSportId() {
        return sportId;
    }

    public int getPeriod() {
        return period;
    }

    public String getName() {
        return name;
    }

    public Long getCategorySetId() { return categorySetId; }

    public int getSort() { return sort; }

    public static PeriodEnum transferCategorySetId(int sportId, int periodId) {
        for (PeriodEnum periodEnum : PeriodEnum.values()) {
            if (periodEnum.getSportId() == sportId&&periodEnum.getPeriod()==periodId) {
                return periodEnum;
            }
        }
        return null;
    }
    public static int getSort(Long categorySetId){
        for (PeriodEnum periodEnum : PeriodEnum.values()) {
            if (categorySetId.equals(periodEnum.getCategorySetId())) {
                return periodEnum.getSort();
            }
        }
        return -1;
    }
}
