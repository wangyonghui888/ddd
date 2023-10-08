package com.panda.sport.rcs.enums;

/**
 * @author :  dorich
 * @project Name :  rcs-parent
 * @package Name :  com.panda.sport.rcs.enums
 * @description :  订单结算输赢情况枚举
 * @date: 2020-03-17 10:34
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum OrderSettleStatus { 

    USER_LOSE(1, "LOSE","用户输"),
    USER_HALF_LOSE(2, "HALF_LOSE","用户输半"),
    USER_WIN(3, "WIN","用户赢"),
    USER_HALF_WIN(4, "HALF_WIN","用户赢半"),
    USER_BACK(5, "BACK","用户走水");

    /**
     * 具体值
     */
    private Integer value;

    /**
     * 输赢状况的名称
     */
    private String name;

    /**
     * 枚举描述
     */
    private String description;

    OrderSettleStatus(Integer value, String name, String description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }


    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    }
