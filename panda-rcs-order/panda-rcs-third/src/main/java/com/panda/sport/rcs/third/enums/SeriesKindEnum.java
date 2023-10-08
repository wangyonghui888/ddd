package com.panda.sport.rcs.third.enums;

/**
 * 单串关种类枚举
 * @author vere
 * @version 1.0.0
 * @date 2023-05-12
 */
public enum SeriesKindEnum {

    /**
     * 单关
     */
    SINGLE(1,"单关"),
    /**
     * 串关
     */
    DOUBLE(2,"串关");

    SeriesKindEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * 串关类型
     */
    private int type;
    /**
     * 描述
     */
    private String value;

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
    public static String getValue(int type) {
        for (SeriesKindEnum seriesTypeEnum:values()){
            if (seriesTypeEnum.type == type){
                return seriesTypeEnum.value;
            }
        }
        return null;
    }
}
