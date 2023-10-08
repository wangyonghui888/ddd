package com.panda.sport.rcs.enums;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  TODO
 * @Date: 2020-02-01 14:24
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum HalfTimeEnum {
    CLOSE(0, "关闭"),
    OPEN(1, "开启");
    private Integer value;
    private String halfTime;

    HalfTimeEnum(Integer value, String halfTime) {
        this.value = value;
        this.halfTime = halfTime;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getHalfTime() {
        return halfTime;
    }

    public void setHalfTime(String halfTime) {
        this.halfTime = halfTime;
    }
}
