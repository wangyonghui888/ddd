package com.panda.sport.rcs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Project Name : panda-rcs-trade-group
 * @Package Name : panda-rcs-trade-group
 * @Description : 赛种
 * @Author : Paca
 * @Date : 2021-02-19 10:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Getter
@AllArgsConstructor
public enum SportIdEnum {

    FOOTBALL(1L, "足球"),
    BASKETBALL(2L, "篮球"),
    _BASEBALL(3L, "棒球"),
    ICE_HOCKEY(4L, "冰球"),
    TENNIS(5L, "网球"),
    A_FOOTBALL(6L, "美式足球"),
    SNOOKER(7L, "斯洛克"),
    TALBE_TENNIS(8L, "乒乓球"),
    VOLLEYBALL(9L, "排球"),
    BADMINTON(10L, "羽毛球");

    private Long id;
    private String name;

    public static boolean isFootball(Long sportId) {
        return FOOTBALL.getId().equals(sportId);
    }

    public static boolean isFootball(Integer sportId) {
        return sportId != null && FOOTBALL.getId().intValue() == sportId;
    }

    public static boolean isBasketball(Long sportId) {
        return BASKETBALL.getId().equals(sportId);
    }

    public static boolean isBasketball(Integer sportId) {
        return sportId != null && BASKETBALL.getId().intValue() == sportId;
    }

    public boolean isYes(Long sportId) {
        return this.getId().equals(sportId);
    }

    public boolean isYes(Integer sportId) {
        return sportId != null && this.getId().intValue() == sportId;
    }
}
