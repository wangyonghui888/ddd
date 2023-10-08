package com.panda.sport.rcs.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @author :  enzo
 */
public enum TennisEnum {

    FULL_TIME(1, Arrays.asList(32,40),50001L, "全场"),
    SET_ONE(2,  Arrays.asList(8),50002L,"第一盘"),
    SET_TWO(3,  Arrays.asList(9),50003L,"第二盘"),
    SET_THREE(4,  Arrays.asList(10),50004L,"第三盘"),
    SET_FOUR(5,  Arrays.asList(11),50005L,"第四盘"),
    SET_FIVE(6,  Arrays.asList(12),50006L,"第五盘"),
    ;

    private int sort;
    private List<Integer> period;
    private Long categorySetId;
    private String name;

    TennisEnum(int sort, List<Integer> period, Long categorySetId, String name) {
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

    public static TennisEnum getEnumByPeriod(int period) {
        for (TennisEnum periodEnum : TennisEnum.values()) {
            if (periodEnum.getPeriod().contains(period)) {
                return periodEnum;
            }
        }
        return null;
    }
    public static int getSort(Long categorySetId){
        for (TennisEnum periodEnum : TennisEnum.values()) {
            if (categorySetId.equals(periodEnum.getCategorySetId())) {
                return periodEnum.getSort();
            }
        }
        return -1;
    }
}
