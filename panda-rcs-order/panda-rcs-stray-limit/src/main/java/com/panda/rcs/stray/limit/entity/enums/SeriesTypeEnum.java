package com.panda.rcs.stray.limit.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeriesTypeEnum {
    TWO_1(2, "2串1"),
    THERE_N(3, "3串1"),
    FOUR_N(4, "4串N"),
    FIVE_N(5, "5串N"),
    SIX_N(6, "6串N"),
    SEVEN_N(7, "7串N"),
    EIGHT_N(8, "8串N"),
    NINE_N(9, "9串N"),
    TEN_N(10, "10串N");

    private final Integer code;

    private final String value;
}
