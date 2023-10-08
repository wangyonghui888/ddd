package com.panda.sport.rcs.oddin.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 注解校验入参为枚举参数的demo enum
 * @Auther: Conway
 * @Date: 2023/08/13 17:33
 */
public enum EnumMemberType {
    NORMAL_VIP(1, "普通会员"),
    SUPPER_VIP(2, "网盘"),
            ;

    private Integer value;
    private String text;

    private static Map<Integer, EnumMemberType> pool = new HashMap<Integer, EnumMemberType>();

    static {
        for (EnumMemberType each : EnumMemberType.values()) {
            EnumMemberType defined = pool.get(each.getValue());
            if (null != defined) {
                throw new IllegalArgumentException(defined.toString() + " defined as same code with "
                        + each.toString());
            }
            pool.put(each.getValue(), each);
        }
    }

    EnumMemberType(Integer value, String text) {
        this.value = value;
        this.text = text;
    }


    public static EnumMemberType valueOf(int value) {
        return pool.get(value);
    }

    public Integer getValue() {
        return this.value;
    }

    public String getText() {
        return text;
    }

    public static boolean isSupport(Integer val) {
        for (EnumMemberType openState : values()) {
            if (openState.getValue().equals(val)) {
                return true;
            }
        }
        return false;
    }

    public static String getText(Integer val) {
        for (EnumMemberType openState : values()) {
            if (openState.getValue().equals(val)) {
                return openState.getText();
            }
        }
        return "";
    }
}
