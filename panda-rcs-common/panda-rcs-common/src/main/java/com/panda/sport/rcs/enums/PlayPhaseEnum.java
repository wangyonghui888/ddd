package com.panda.sport.rcs.enums;

/**
 * @author :  enzo
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.constants
 * @Description :  比赛玩法阶段
 * @Date:
 */
public enum PlayPhaseEnum {

    AUDIENCE(1, "全场"),
    FIRST_HALF(2, "上半场"),
    AUDIENCE_CORNER(3, "全场角球"),
    CORNER_HALF(4, "上半场角球"),
    PENALTY(5, "罚球");

    private Integer code;

    private String value;

    PlayPhaseEnum(Integer code, String value) {
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
