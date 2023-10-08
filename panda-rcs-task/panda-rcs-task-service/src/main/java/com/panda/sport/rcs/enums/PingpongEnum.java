package com.panda.sport.rcs.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @author :  enzo
 */
public enum PingpongEnum {

    FULL_TIME(1, Arrays.asList(32,40),80001L, "全场"),
    SET_ONE(2,  Arrays.asList(8),80002L,"第一局"),
    SET_TWO(3,  Arrays.asList(9),80003L,"第二局"),
    SET_THREE(4,  Arrays.asList(10),80004L,"第三局"),
    SET_FOUR(5,  Arrays.asList(11),80005L,"第四局"),
    SET_FIVE(6,  Arrays.asList(12),80006L,"第五局"),
    SET_SIX(7,  Arrays.asList(12),80007L,"第六局"),
    SET_SEVEN(8,  Arrays.asList(12),80008L,"第七局"),
    ;

    private int sort;
    private List<Integer> period;
    private Long categorySetId;
    private String name;

    PingpongEnum(int sort, List<Integer> period, Long categorySetId, String name) {
        this.sort = sort;
        this.period = period;
        this.categorySetId = categorySetId;
        this.name = name;
    }

    public int getSort() {
        return sort;
    }

    public List<Integer> getPeriod() {
        return period;
    }

    public Long getCategorySetId() {
        return categorySetId;
    }

    public String getName() {
        return name;
    }

    public static PingpongEnum getEnumByPeriod(int period) {
        for (PingpongEnum periodEnum : PingpongEnum.values()) {
            if (periodEnum.getPeriod().contains(period)) {
                return periodEnum;
            }
        }
        return null;
    }
    public static int getSort(Long categorySetId){
        for (PingpongEnum periodEnum : PingpongEnum.values()) {
            if (categorySetId.equals(periodEnum.getCategorySetId())) {
                return periodEnum.getSort();
            }
        }
        return -1;
    }
}
