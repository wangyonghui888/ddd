package com.panda.sport.rcs.mgr.enums;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.enums
 * @Description :  TODO
 * @Date: 2020-10-16 10:34
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum DataTypeEnum {
    MERCHANTS(0,"商户限额"),
    MERCHANT_SINGLE_LIMIT(1,"商户单场限额"),
    USER_DAILY_LIMIT(2,"用户单日限额"),
    USER_SINGLE_LIMIT(3,"用户单场限额"),
    USER_SINGLE_BET_LIMIT(4,"用户单注单关限额"),
    SERIES_PAYMENT_LIMIT(5,"串关单注赔付限额"),
    SERIES_RATIO(6,"各投注项计入单关限额的投注比例"),
    BET_AMOUNT_LIMIT(7,"最低/最高投注额限制"),
    SERIES_CONNECTION_RATIO(8,"串关已用额度比例")
    ;
    private Integer type;
    private String value;

    private DataTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
