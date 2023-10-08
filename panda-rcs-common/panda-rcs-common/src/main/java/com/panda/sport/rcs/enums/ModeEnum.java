package com.panda.sport.rcs.enums;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  TODO
 * @Date: 2020-02-01 14:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum ModeEnum {

    AUTOMATIC(0, "自动"),

    MANUAL(1, "手动");

    private Integer value;
    private String mode;

    ModeEnum(Integer value, String mode) {
        this.value = value;
        this.mode = mode;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
