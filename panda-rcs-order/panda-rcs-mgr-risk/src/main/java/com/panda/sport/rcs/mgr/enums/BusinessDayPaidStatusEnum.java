package com.panda.sport.rcs.mgr.enums;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.enums
 * @Description :  TODO
 * @Date: 2020-09-04 15:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum BusinessDayPaidStatusEnum {
    /**
     * 正常
     */
    NORMAL("0",1),
    /**
     * 危险
     */
    DANGER("0.6",2),
    /**
     * 高危
     */
    HIGH_RISK("0.8",3);
    /**
     * 最低比例
     */
    private String min;
    /**
     * 状态值
     */
    private Integer status;

    private BusinessDayPaidStatusEnum(String min, Integer status) {
        this.min = min;
        this.status = status;
    }

    public String getMin() {
        return min;
    }

    public Integer getStatus() {
        return status;
    }
}
