package com.panda.sport.rcs.oddin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author :wiker
 * @Date: 2023-05 11:07
 * 自动接受赔率变化枚举
 **/
@AllArgsConstructor
@Getter
public enum AcceptOddsChange {
    ACCEPT_ODDS_CHANGE_UNSPECIFIED(0,"未指定"),
    ACCEPT_ODDS_CHANGE_NONE(1,"自动接受赔率无变动"),
    ACCEPT_ODDS_CHANGE_ANY(2,"自动接受任何赔率变动"),
    ACCEPT_ODDS_CHANGE_HIGHER(3,"自动接收更高的赔率变化");

    private Integer code;
    private String message;
}
