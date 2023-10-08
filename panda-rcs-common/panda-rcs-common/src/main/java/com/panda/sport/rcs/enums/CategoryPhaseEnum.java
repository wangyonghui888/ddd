package com.panda.sport.rcs.enums;

/**
 * @author :  enzo
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.constants
 * @Description :  比赛玩法阶段
 * @Date:
 */
public enum CategoryPhaseEnum {

    AUDIENCE(1, "主要玩法"),
    CORNER(2, "角球"),
    PENALTY(3, "罚球");

    private Integer code;

    private String value;

    CategoryPhaseEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
