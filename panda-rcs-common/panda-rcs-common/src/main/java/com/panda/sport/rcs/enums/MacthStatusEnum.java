package com.panda.sport.rcs.enums;

/**
 * @author :  myname
 * @Project Name :  赛事状态
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  TODO
 * @Date: 2019-11-19 14:06
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum MacthStatusEnum {
    //赛事状态   转换为融合需要的
    NOT_OPEN(0, "锁", 11),
    OPEN(1, "开盘",0),
    CLOSE(2, "关盘",2),
    SEAL(3, "封盘",1);
    private int state;
    private String name;
    private int status;

    private MacthStatusEnum(int state, String name,int status) {
        this.state = state;
        this.name = name;
        this.status = status;
    }

    public int getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public static MacthStatusEnum getEnum(int state) {
        for (MacthStatusEnum macthStatusEnum : values()) {
            if (macthStatusEnum.getState() == state) {
                return macthStatusEnum;
            }
        }
        return null;
    }

    public static int getState(int status) {
        for (MacthStatusEnum macthStatusEnum : values()) {
            if (macthStatusEnum.getStatus() == status) {
                return macthStatusEnum.getState();
            }
        }
        return -1;
    }
}
