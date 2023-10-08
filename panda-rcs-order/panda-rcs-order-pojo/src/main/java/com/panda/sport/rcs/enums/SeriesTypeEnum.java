package com.panda.sport.rcs.enums;
public enum SeriesTypeEnum {
    TWO(2,"2串1"),
    THREE(3,"3串N"),
    FOUR(4,"4串N"),
    FIVE(5,"5串N"),
    SIX(6,"6串N"),
    SEVEN(7,"7串N"),
    EIGHT(8,"8串N及以上");

    SeriesTypeEnum(int type, String value) {
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
        for (SeriesTypeEnum seriesTypeEnum:values()){
            if (seriesTypeEnum.type == type){
                return seriesTypeEnum.value;
            }
        }
        return null;
    }

}
