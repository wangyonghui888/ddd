package com.panda.sport.rcs.enums.limit;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.rcs.enums.limit
 * @Description : Redis 命令枚举
 * @Author : Paca
 * @Date : 2020-10-17 16:49
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Getter
@AllArgsConstructor
public enum RedisCmdEnum {

    INCRBY("incrBy"),
    INCRBYFLOAT("incrByFloat"),
    HINCRBY("hincrBy"),
    HINCRBYFLOAT("hincrByFloat");

    private String cmd;

    public static boolean isIncrBy(String cmd) {
        return INCRBY.getCmd().equalsIgnoreCase(cmd);
    }

    public static boolean isIncrByFloat(String cmd) {
        return INCRBYFLOAT.getCmd().equalsIgnoreCase(cmd);
    }

    public static boolean isHincrBy(String cmd) {
        return HINCRBY.getCmd().equalsIgnoreCase(cmd);
    }

    public static boolean isHincrByFloat(String cmd) {
        return HINCRBYFLOAT.getCmd().equalsIgnoreCase(cmd);
    }
}
