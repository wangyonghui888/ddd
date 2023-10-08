package com.panda.sport.rcs.third.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Beulah
 * @date 2023/3/31 20:08
 * @description todo
 */

@Getter
@AllArgsConstructor
public enum CurrencyEnum {

    USD("USD", "美元"),
    CNY("CNY", "人民币"),
    PHP("PHP", "披索")
    ;

    private String code;
    private String text;

}
