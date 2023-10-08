package com.panda.sport.rcs.mgr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogTypeEnum {
    CHAMPION_TYPE(3, "冠军赛事");
    private Integer code;
    private String value;
}
