package com.panda.sport.rcs.enums;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  盘口状态
 * @Date: 2019-10-26 15:33
 * @ModificationHistory Who    When    What
 */

public enum MarketStatusEnum {
    //盘口状态
    OPEN(0, "开盘"),
    //不打勾
    CLOSE(2, "关盘"),
    SEAL(1, "封盘"),
    LOCK(11, "锁盘");

    private int state;
    private String name;

    MarketStatusEnum(int state, String name) {
        this.state = state;
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public String getName() {
        return name;
    }
}