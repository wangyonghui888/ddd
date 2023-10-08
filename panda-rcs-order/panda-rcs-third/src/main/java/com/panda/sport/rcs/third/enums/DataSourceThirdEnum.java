package com.panda.sport.rcs.third.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 第三方数据源枚举
 * @author vere
 * @date 2023-05-26
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DataSourceThirdEnum {
    /**
     * redCat->RC
     */
    RC("RC", "red_cat"),
    ;
    private String code;
    private String text;
}
