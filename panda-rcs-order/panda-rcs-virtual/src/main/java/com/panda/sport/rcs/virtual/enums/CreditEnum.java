package com.panda.sport.rcs.virtual.enums;

/**
 * @author :  Jesson
 * @Project Name :  panda_vs_node
 * @Package Name :  com.panda.gr.wallet.enums
 * @Description :  TODO
 * @Date: 2020-10-01 12:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum CreditEnum {
    CREDIT_SUCCESS(0L, "SUCCESS", "Transaction acepted. Operation succesfully executed. Result=success"),
    CREDIT_INVALID_SESSION(2L, "INVALID_SESSION", "Transaction rejected. User's session not found or it's expired. Result=error"),
    CREDIT_TRANSACTION_ALREADY_EXISTS(3L, "TRANSACTION_ALREADY_EXISTS", "Transaction rejected. A transaction was found for the same ticket. Result=error"),
    CREDIT_TICKET_NOT_FOUND(4L, "TICKET_NOT_FOUND", "Transaction rejected. Ticket not exist. Result=error."),
    ;

    private Long code;
    private String message;
    private String description;

    CreditEnum(Long code, String message, String description) {
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
