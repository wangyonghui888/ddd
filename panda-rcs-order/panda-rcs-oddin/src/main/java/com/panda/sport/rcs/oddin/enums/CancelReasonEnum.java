package com.panda.sport.rcs.oddin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author :conway
 * @Date: 2023-05-03 13:51
 * 订单取消原因
 **/
@AllArgsConstructor
@Getter
public enum CancelReasonEnum {

    CANCEL_REASON_UNSPECIFIED(0, "未指定"),
    CANCEL_REASON_TICKET_TIMEOUT(1, "请求超时"),
    CANCEL_REASON_WRONG_TICKET(2, "错误订单"),
    CANCEL_REASON_TECHNICAL_ISSUE(3, "技术问题"),
    CANCEL_REASON_UNEXPECTED_ISSUE(4, "意外问题"),
    CANCEL_REASON_REGULATOR(5, "调节"),
    CANCEL_REASON_FOREIGN_STAKE_REJECTED(6, "拒绝外国股权"),
    UNRECOGNIZED(-1, "无法识别");
    private Integer code;
    private String message;
}
