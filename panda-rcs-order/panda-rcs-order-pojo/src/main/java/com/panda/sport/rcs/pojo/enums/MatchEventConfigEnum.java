package com.panda.sport.rcs.pojo.enums;

/**
 * @author :  myname
 * @Project Name :  事件配置常量
 * @Package Name :  com.panda.sport.rcs.trade.enums
 * @Description :
 * @Date: 2020-08-16 14:23
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum MatchEventConfigEnum {
    /**安全事件*/
    EVENT_SAFETY("safety",3,0),
    /**危险事件*/
    EVENT_DANGER("danger",10,1),
    /**封盘事件*/
    EVENT_CLOSING( "closing",120,2),
    /**拒单事件*/
    EVENT_REJECT("reject",0,3),

    /**PA事件*/
    EVENT_SOURCE_PA("PA",0),
    /**SR事件*/
    EVENT_SOURCE_SR("SR",1),
    /**BG事件*/
    EVENT_SOURCE_BC( "BC",2),
    /**BC事件*/
    EVENT_SOURCE_BG("BG",3),
    /**RB事件*/
    EVENT_SOURCE_RB("RB",4),
    /**KO事件*/
    EVENT_SOURCE_KO("KO",5),
    /**PD2事件*/
    EVENT_SOURCE_PD_TWO("PD2",101),
    /**PD事件*/
    EVENT_SOURCE_PD("PD",99),


    /**订单初始状态*/
    ORDER_INIT_VALIDATERESULT("validateResult",0),
    /**订单初始状态*/
    ORDER_INIT_ORDER_STATUS("orderStatus",0),
    /**订单拒单状态*/
    ORDER_REJECT_ORDER_STATUS("orderRejectStatus",2),
    /**订单接单状态*/
    ORDER_ACCEPT_ORDER_STATUS("orderAcceptStatus",1),
    /**订单已校验状态*/
    ORDER_CHECKED_VALIDATERESULT("checkedValidateResult",1),
    /**订单未派彩*/
    ORDER_UN_PAYOUT_STATUS("unPayoutStatus",0),
    /**订单结算拒单彩*/
    ORDER_SETTLE_REJECT_ORDER_STATUS( "rejectOrderStatus",9),
    /**其他赛事默认接单时间*/
    ORDER_ACCEPT_DEFAULT_TIME("defaultTime",5),
    /**接单单位转换*/
    ORDER_SECOND_UNIT( "second",1000),
    /**最终接单时间*/
    ORDER_ACCEPT_FINAL_TIME("finalTime",120*1000);

    private String code;
    private Integer value;
    private Integer type;

    MatchEventConfigEnum(String code,Integer value) {
        this.value = value;
        this.code = code;
    }
    MatchEventConfigEnum(String code, Integer value,Integer type) {
        this.value = value;
        this.code = code;
        this.type = type;
    }
    public String getCode() {
        return code;
    }
    public Integer getValue() {
        return value;
    }
    public Integer getType() {
        return type;
    }
}
