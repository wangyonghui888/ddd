package com.panda.rcs.logService.Enum;

import java.util.Arrays;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.trade.enums
 * @Description :  TODO
 * @Date: 2020-08-16 14:23
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum CategorySetIdEnum {
    FULL_TIME(10011L,  "{\"en\":\"Full court goal gameplay\",\"zs\":\"全场进球玩法\",\"zh\":\"全场进球玩法\"}"),
    FIRST_HALF(10012L, "{\"en\":\"First half goal gameplay\",\"zs\":\"上半场进球玩法\",\"zh\":\"上半场进球玩法\"}"),
    SECOND_HALF(10013L, "{\"en\":\"Second half goal gameplay\",\"zs\":\"下半场进球玩法\",\"zh\":\"下半场进球玩法\"}"),
    AND_OR(10014L,  "{\"en\":\"&Gameplay\",\"zs\":\"&玩法\",\"zh\":\"&玩法\"}"),

    goal(10001L,  "{\"en\":\"goal\",\"zs\":\"进球类\",\"zh\":\"进球类\"}"),
    SPECIAL_TYEP(10016L, "{\"en\":\"Special gameplay\",\"zs\":\"特殊玩法\",\"zh\":\"特殊玩法\"}"),

    TIME_TYEP(10015L, "{\"en\":\"Time based gameplay\",\"zs\":\"时间类玩法\",\"zh\":\"时间类玩法\"}"),
    CORNER_KICK(10017L, "{\"en\":\"Corner kick gameplay\",\"zs\":\"角球玩法\",\"zh\":\"角球玩法\"}"),
    PENALTY_CARD(10018L, "{\"en\":\"Penalty card\",\"zs\":\"罚牌\",\"zh\":\"罚牌\"}"),
    PENALTY_SHOOT(10020L,  "{\"en\":\"point sphere\",\"zs\":\"点球\",\"zh\":\"点球\"}"),
    PROMOTION_TYPE(10021L,  "{\"en\":\"be promoted\",\"zs\":\"晋级\",\"zh\":\"晋级\"}"),
    CORRECT_SCORE(10022L,  "{\"en\":\"Wave gallbladder\",\"zs\":\"波胆\",\"zh\":\"波胆\"}"),
    TIME_5MINS(10023L,  "{\"en\":\"5 minute gameplay\",\"zs\":\"5分钟玩法\",\"zh\":\"5分钟玩法\"}"),
    Featured(10024L,  "{\"en\":\"Featured combination\",\"zs\":\"特色组合\",\"zh\":\"特色组合\"}"),
    EXTRA_TIME(10019L, "{\"en\":\"Extra time goal\",\"zs\":\"加时进球\",\"zh\":\"加时进球\"}");
    private Long categorySetId;
    private String value;
    CategorySetIdEnum(Long categorySetId, String value) {
        this.categorySetId = categorySetId;
        this.value = value;
    }

    public static String getValue(Long categorySetId) {
        for (CategorySetIdEnum autoCloseMarketEnum:values()){
            if (autoCloseMarketEnum.categorySetId.equals(categorySetId)){
                return autoCloseMarketEnum.value;
            }
        }
        return "-";
    }
}
