package com.panda.sport.rcs.enums.limit;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户特殊限额类型枚举
 */
@Getter
@AllArgsConstructor
public enum UserSpecialLimitType {

    NO("0","无"),
    LABEL("1","标签限额"),
    PERCENTAGE("2","特殊百分比限额"),
    SINGLE("3","特殊单注单场限额"),
    VIP("4","特殊vip限额");

    public String type;
    public String desc;

}
