package com.panda.sport.rcs.mgr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.enums
 * @Description :  取消结算操作状态
 * @Date: 2020-12-10 上午 11:36
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Getter
@AllArgsConstructor
public enum SettleOperateStatusEnum {
    BET_SUCCESS(0, "下注成功"),
    /**
     * 结算
     */
    Settle(1, "结算"),
    /**
     * 结算回滚
     */
    SettleRollback(2, "结算回滚"),
    /**
     * 取消回滚
     */
    CancelSettleRollback(4, "取消回滚");

    private Integer code;
    private String desc;

    public static boolean isBetSuccess(Integer code) {
        return BET_SUCCESS.getCode().equals(code);
    }
}
