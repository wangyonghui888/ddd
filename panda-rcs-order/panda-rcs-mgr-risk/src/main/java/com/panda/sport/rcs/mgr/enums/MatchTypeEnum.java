package com.panda.sport.rcs.mgr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum MatchTypeEnum {
    EARLY_MARKET(1, "早盘赛事"),
    ROLLING_MARKET(2, "滚球赛事"),
    CHAMPION_MARKET(3, "冠军赛事");
    private final Integer code;
    private final String value;

    public static boolean isEarlyMarket(Integer code){
        return Objects.equals(EARLY_MARKET.code, code);
    }
    public static boolean isRollingMarket(Integer code){
        return Objects.equals(ROLLING_MARKET.code, code);
    }
    public static boolean isChampionMarket(Integer code){
        return Objects.equals(CHAMPION_MARKET.code, code);
    }
}
