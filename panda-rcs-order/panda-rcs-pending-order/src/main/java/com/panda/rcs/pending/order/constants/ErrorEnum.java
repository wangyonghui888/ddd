package com.panda.rcs.pending.order.constants;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.rcs.pending.order.constants
 * @Description :  TODO
 * @Date: 2022-06-17 13:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum ErrorEnum {


    PEN_COUNT_ERROR(40080, "当前赛事预约中订单数量超过上限"),
    AMOUNT_ERROR(40081, "您当前赛事或玩法预约额度已用完");
    private Integer code;
    private String msg;

    ErrorEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
