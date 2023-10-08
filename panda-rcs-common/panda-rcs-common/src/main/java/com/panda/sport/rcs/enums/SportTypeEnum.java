package com.panda.sport.rcs.enums;

/***
 * 赛种枚举
 */
public enum SportTypeEnum {

    FOOTBALL(1, "足球"),
    BASKETBALL(2, "篮球"),
    TENNIS(3, "网球"),
    ESPORTS(4, "电子竞技"),
    PINGPONG(5, "乒乓球"),
    BADMINTON(6, "羽毛球"),
    SLOCK(7, "斯洛克"),
    BASEBALL(8, "棒球"),
    VOLLEYBALL(9, "排球");

    private Integer code;
    private String value;

    SportTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static SportTypeEnum getSportTypeEnum(Integer code) {
        for (SportTypeEnum sportTypeEnum : values()) {
            if (sportTypeEnum.getCode().equals(code)) {
                return sportTypeEnum;
            }
        }
        return null;
    }
}
