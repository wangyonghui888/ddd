package com.panda.sport.rcs.enums;

/**
 * @author :  kimi
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.constants
 * @Description :  投注阶段枚举
 * @Date: 2019-10-12 11:32
 */
public enum DateTypeEnum {

    ORDER_YEAR(1, "年","y"),

    ORDER_PHASE(2, "期","p"),

    ORDER_WEEK(3, "周","w"),

    ORDER_DAY(4, "日","d");

    private Integer code;

    private String value;

    private String instr;

    DateTypeEnum(Integer code, String value, String instr) {
        this.code = code;
        this.value = value;
        this.instr = instr;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getInstr() {
        return instr;
    }

    public static DateTypeEnum getDateTypeEnum(String instr) {
        for (DateTypeEnum dateTypeEnum : values()) {
            if (dateTypeEnum.getInstr().equals(instr) ) {
                return dateTypeEnum;
            }
        }
        return null;
    }
}
