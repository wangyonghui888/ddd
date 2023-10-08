package com.panda.sport.rcs.enums;

/**
 * @author :  enzo
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  TODO
 * @Date: 2019-10-09 10:14
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum BusinessDayPaidStatus {
    HIGHRISK(1, "高危"),
    DANGER(2, "危险"),
    NORMAL(3, "正常"),
    HEALTH(4, "健康"),
    OVERLOAD(5, "满负荷制动"),
    MANUAL(6, "手动制动"),
    DEACTIVATED(7, "已停用"),
    ;
    private Integer code;
    private String value;

    BusinessDayPaidStatus(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
