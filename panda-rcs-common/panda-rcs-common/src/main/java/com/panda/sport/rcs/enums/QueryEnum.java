package com.panda.sport.rcs.enums;

/**
 * @author :  enzo
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.constants
 * @Description :  比赛玩法阶段
 * @Date:
 */
public enum QueryEnum {

    OPEN(1, "开放中"),
    CLOSE(0, "关闭中");

    private Integer code;

    private String value;

    QueryEnum(Integer code, String value) {
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
