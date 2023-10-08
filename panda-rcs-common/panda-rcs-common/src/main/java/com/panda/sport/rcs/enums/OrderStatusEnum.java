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
public enum OrderStatusEnum {
    ORDER_WAITING(0, "待处理"),

    ORDER_ACCEPT(1, "成功"),

    ORDER_REJECT(2, "拒绝");

    private Integer code;
    private String mode;

    OrderStatusEnum(Integer code, String mode) {
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
