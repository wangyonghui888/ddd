package com.panda.sport.rcs.oddin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author :wiker
 * @Date: 2023-24 01:51
 * 奖金支付模式,目前只支持ALL
 **/
@AllArgsConstructor
@Getter
public enum BetBonusMode {

    BET_BONUS_MODE_UNSPECIFIED(0,"未指定"),
    BET_BONUS_MODE_ALL(1,"全部的");
    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
