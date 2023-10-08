package com.panda.sport.data.rcs.dto;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  TODO
 * @Date: 2020-02-01 14:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum ChannelCodeEnum {
    MTS(0,"MTS"),

    PA(1,"PANDA");

    private Integer code;
    private String mode;

    ChannelCodeEnum(Integer code, String mode) {
        this.code = code;
        this.mode = mode;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
