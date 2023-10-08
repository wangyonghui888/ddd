package com.panda.sport.rcs.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @author :  enzo
 */
public enum BasketballEnum {


    SECTION_ONE(1,  Arrays.asList(13),20003L,"第一节"),
    SECTION_TWO(2,  Arrays.asList(14,301),20004L,"第二节"),
    FIRST_HALF(3 ,Arrays.asList(1),20002L,"上半场"),
    SECTION_THREE(4,  Arrays.asList(15,302,31),20005L,"第三节"),
    SECTION_FOUR(5,  Arrays.asList(16,303),20006L,"第四节"),
    SECOND_HALF(6,  Arrays.asList(2),20007L,"下半场"),
    FULL_TIME(7, Arrays.asList(32,40),20001L, "全场"),
    ;

    private int sort;
    private List<Integer> period;
    private Long categorySetId;
    private String name;

    BasketballEnum(int sort, List<Integer> period, Long categorySetId, String name) {
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

    public static BasketballEnum getEnumByPeriod(int period) {
        for (BasketballEnum periodEnum : BasketballEnum.values()) {
            if (periodEnum.getPeriod().contains(period)) {
                return periodEnum;
            }
        }
        return null;
    }
    public static int getSort(Long categorySetId){
        for (BasketballEnum periodEnum : BasketballEnum.values()) {
            if (categorySetId.equals(periodEnum.getCategorySetId())) {
                return periodEnum.getSort();
            }
        }
        return -1;
    }
}
