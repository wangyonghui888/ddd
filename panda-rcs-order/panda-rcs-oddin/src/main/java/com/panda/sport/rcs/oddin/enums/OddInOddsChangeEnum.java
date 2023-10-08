package com.panda.sport.rcs.oddin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author :wiker
 * @Date: 2023-21 13:12
 * 赔率的变化
 **/
@AllArgsConstructor
@Getter
public enum OddInOddsChangeEnum {

    IN_ODDS_NOT_CHANGE(0, "无变化"),
    IN_ODDS_CHANGE(1, "有变化"),

    INV_ODDS_HIGHER_CHANGE(2, "更高的赔率变化");


    private int code;
    private String message;
}
