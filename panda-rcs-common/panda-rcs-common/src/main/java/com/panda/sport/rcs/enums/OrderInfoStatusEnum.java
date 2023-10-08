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
public enum OrderInfoStatusEnum {

    WAITING(0, "待处理"),

    EARLY_PASS(1, "早盘接单"),

    EARLY_REFUSE(2, "早盘拒单"),

    MTS_PASS(3, "MTS接单"),

    MTS_REFUSE(4, "MTS拒单"),

    BS_REFUSE(5, "业务拒单"),

    RISK_PROCESSING(6, "风控接拒单处理中"),

    MTS_PROCESSING(7, "MTS处理中"),

    SCROLL_PASS(8, "滚球接单成功"),

    SCROLL_REFUSE(9, "滚球拒单"),
    
    SCROLL_HAND_PASS(10,"滚球手工接拒单-接单") ,

    SCROLL_HAND_REFUSE(11, "滚球手工接拒单-拒单"),
    
	ALL_PASS(12, "一键秒接"),
	
	TRADE_CANCEL_ORDER(13, "操盘手动取消注单"),
	
	PAUSE_ORDER(14, "暂停接拒");

    private Integer code;
    private String mode;

    OrderInfoStatusEnum(Integer code, String mode) {
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
