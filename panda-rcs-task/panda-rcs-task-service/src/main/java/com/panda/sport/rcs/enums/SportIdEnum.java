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
    // 避免返回null
    DEFAULT(0L, "默认"),

    FOOTBALL(1L, "足球"),
    BASKETBALL(2L, "篮球"),
    BASEBALL(3L, "棒球"),
    ICE_HOCKEY(4L, "冰球"),
    TENNIS(5L, "网球"),
    AMERICAN_FOOTBALL(6L, "美式足球"),
    SNOOKER(7L, "斯洛克"),
    PING_PONG(8L, "乒乓球"),
    VOLLEYBALL(9L, "排球"),
    BADMINTON(10L, "羽毛球"),
    HANDBALL(11L, "手球"),
    BOXING(12L, "拳击"),
    BEACH_VOLLEYBALL(13L, "沙滩排球"),
    RUGBY_UNION(14L, "联合式橄榄球"),
    HOCKEY(15L, "曲棍球"),
    WATER_POLO(16L, "水球");

    private Long id;
    private String name;

    public boolean isYes(Long sportId) {
        return this.getId().equals(sportId);
    }

    public boolean isYes(Integer sportId) {
        return sportId != null && this.getId().intValue() == sportId;
    }

    public boolean isNo(Long sportId) {
        return !this.isYes(sportId);
    }

    public boolean isNo(Integer sportId) {
        return !this.isYes(sportId);
    }

    public static boolean isFootball(Long sportId) {
        return FOOTBALL.isYes(sportId);
    }

    @Deprecated
    public static boolean isFootball(Integer sportId) {
        return FOOTBALL.isYes(sportId);
    }


    public static boolean isBasketball(Long sportId) {
        return BASKETBALL.isYes(sportId);
    }

    @Deprecated
    public static boolean isBasketball(Integer sportId) {
        return BASKETBALL.isYes(sportId);
    }

    public static boolean isTennis(Long sportId) {
        return TENNIS.isYes(sportId);
    }


    public static boolean isPingpong(Long sportId) {
        return PING_PONG.isYes(sportId);
    }


    public static boolean isVolleyball(Long sportId) {
        return VOLLEYBALL.isYes(sportId);
    }

    public static boolean isSnooker(Long sportId) {
        return SNOOKER.isYes(sportId);
    }

    public static boolean isBaseBall(Long sportId) {
        return BASEBALL.isYes(sportId);
    }

    public static boolean isBadminton(Long sportId) {
        return BADMINTON.isYes(sportId);
    }
    public static boolean isIceHockey(Long sportId) {
        return ICE_HOCKEY.isYes(sportId);
    }
}
