package com.panda.rcs.pending.order.enums;

import lombok.Getter;

/**
 * 赛事状态枚举类
 */
@Getter
public enum MatchStatusEnum {

    UnStart(0, "未开赛"),
    Starting(1, "已开赛（滚球）"),
    Stopping(2, "暂停"),
    Ending(3, "结束"),
    Close(4, "关闭"),
    ;

    private int code;
    private String value;

    MatchStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

}
