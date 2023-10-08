package com.panda.sport.rcs.oddin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Beulah
 * @date 2023/3/22 14:35
 * @description 第三api描述
 */
@Getter
@AllArgsConstructor
public enum ThirdApiEnum {

    BE("BE", "Beter"),

    GTS("GTS", "betAssessment"),
    BC("BC", "BC-BetGuard");

    //三方标志
    private String code;

    //描述
    private String desc;
}
