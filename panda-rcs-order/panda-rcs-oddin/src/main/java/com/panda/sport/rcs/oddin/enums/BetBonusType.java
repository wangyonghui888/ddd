package com.panda.sport.rcs.oddin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author :wiker
 * @Date: 2023-24 01:42
 * 奖金类型目前只支持 TOTAL
 **/
@AllArgsConstructor
@Getter
public enum BetBonusType {
    BET_BONUS_TYPE_UNSPECIFIED(0,"未指定"),
    BET_BONUS_TYPE_TOTAL(1,"全部类型");
    private Integer code;
    private String message;

}
