package com.panda.sport.rcs.virtual.enums;

/**
 * @author :  Jesson
 * @Project Name :  panda_vs_node
 * @Package Name :  com.panda.gr.wallet.enums
 * @Description :  TODO
 * @Date: 2020-10-01 12:58
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum SolveEnum {
    SOLVE_SUCCESS(0L, "SUCCESS", "Transaction accepted. Operation succesfully executed. Result=success"),
    SOLVE_TICKET_NOT_FOUND(4L, "TICKET_NOT_FOUND", "Transaction rejected. Transaction not exist. Result=error."),
    ;
    private Long code;
    private String message;
    private String description;

    SolveEnum(Long code, String message, String description) {
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
