package com.panda.sport.rcs.third.enums;

/**
 * @author Beulah
 * @date 2023/6/5 18:11
 * @description 订单最终状态标识
 */
public enum OrderStatusEnum {

    WAITING(0, "等待"),
    ACCEPTED(1, "接单"),
    REJECTED(2, "拒单")
    ;


    //注单状态编码
    private int code;

    //注单状态描述
    private String text;

    private OrderStatusEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
