package com.panda.sport.rcs.virtual.enums;

/**
 * @author :  Jesson
 * @Project Name :  panda_vs_node
 * @Package Name :  com.panda.gr.wallet.enums
 * @Description :  TODO
 * @Date: 2020-10-01 11:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum SellEnum {

    SELL_SUCCESS(0L, "SUCCESS", "Ticket acepted. Operation succesfully executed. Result=success"),
    SELL_NOT_ENOUGH_CREDIT(1L, "NOT_ENOUGH_CREDIT", "Ticket rejected. User has not credit to make the transaction. Result=error"),
    SELL_INVALID_SESSION(2L, "INVALID_SESSION", "Ticket rejected. User's session not found or it's expired. Result=error"),
    SELL_TRANSACTION_ALREADY_EXISTS(3L, "TRANSACTION_ALREADY_EXISTS", "Ticket rejected. A transaction was found for the same ticket. Result=error"),
    ;
    private Long code;
    private String message;
    private String description;

    SellEnum(Long code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public Long getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
