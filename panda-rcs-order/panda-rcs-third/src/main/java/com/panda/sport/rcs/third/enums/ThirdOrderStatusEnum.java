package com.panda.sport.rcs.third.enums;

/**
 * 注单状态枚举
 * @author vere
 * @date 2023-05-27
 * @version 1.0.0
 */

public enum ThirdOrderStatusEnum {
    PENDING(0, "投注中"),
    SUCCESS(1, "成功"),
    REJECTED(2, "拒单");
    ThirdOrderStatusEnum(int type, String value) {
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
        for (ThirdOrderStatusEnum seriesTypeEnum:values()){
            if (seriesTypeEnum.type == type){
                return seriesTypeEnum.value;
            }
        }
        return null;
    }

}
