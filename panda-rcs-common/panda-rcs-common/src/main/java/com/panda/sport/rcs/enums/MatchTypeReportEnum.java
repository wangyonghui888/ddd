package com.panda.sport.rcs.enums;

/**
 * @author :  kimi
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.constants
 * @Description :  投注阶段枚举
 * @Date: 2019-10-12 11:32
 */
public enum MatchTypeReportEnum {
    //
    BEFORE_MATCH(1, "早盘"),

    ROLLING_BALL(2, "滚球盘"),

    CHAMPION_BALL(3, "冠军盘");


    private Integer code;
    private String value;

    MatchTypeReportEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static MatchTypeReportEnum getMatchTypeEnum(Integer code) {
        for (MatchTypeReportEnum matchTypeEnum : values()) {
            if (matchTypeEnum.getCode().equals(code) ) {
                return matchTypeEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
