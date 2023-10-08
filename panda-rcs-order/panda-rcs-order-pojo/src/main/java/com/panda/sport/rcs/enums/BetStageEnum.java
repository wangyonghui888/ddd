package com.panda.sport.rcs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 投注阶段
 * @Author : Paca
 * @Date : 2021-05-06 13:21
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Getter
@AllArgsConstructor
public enum BetStageEnum {

    PRE("pre", "早盘"),
    LIVE("live", "滚球");

    private String code;
    private String name;
}
