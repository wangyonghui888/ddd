package com.panda.sport.rcs.task.enums;

public enum SportTypeEnum {
    FOOT(1, "足球"),
    BASKET(2, "篮球"),
    TENNIS(5, "网球");

    private Integer code;
    private String name;

    SportTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
