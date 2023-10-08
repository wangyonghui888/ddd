package com.panda.sport.rcs.third.enums;

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
    BASEBALL(3L, "棒球"),
    ICE_HOCKEY(4L, "冰球"),
    TENNIS(5L, "网球"),
    AMERICAN_FOOTBALL(6L, "美式足球"),
    SNOOKER(7L, "斯洛克"),
    PING_PONG(8L, "乒乓球"),
    VOLLEYBALL(9L, "排球"),
    BADMINTON(10L, "羽毛球"),
    HANDBALL(11L, "手球"),
    BEACH_VOLLEYBALL(13L, "沙滩排球");

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

    public static boolean isTennis(Long sportId) {
        return TENNIS.getId().equals(sportId);
    }
    public static boolean isSnooker(Long sportId) {
        return SNOOKER.getId().equals(sportId);
    }
    public static boolean isTennis(Integer sportId) {
        return sportId != null && TENNIS.getId().intValue() == sportId;
    }
    public static boolean isSnooker(Integer sportId) {
        return sportId != null && SNOOKER.getId().intValue() == sportId;
    }
    public static boolean isPingPong(Integer sportId) {
        return sportId != null && PING_PONG.getId().intValue() == sportId;
    }
    public static boolean isVolleyBall(Long sportId) {
        return VOLLEYBALL.getId().equals(sportId);
    }
    public static boolean isPingPong(Long sportId) {
        return PING_PONG.getId().equals(sportId);
    }
    public static boolean isVolleyBall(Integer sportId) {
        return sportId != null && VOLLEYBALL.getId().intValue() == sportId;
    }
    public static boolean isBadminton(Integer sportId) {
        return sportId != null && BADMINTON.getId().intValue() == sportId;
    }
    public static boolean isBaseBall(Integer sportId) {
        return sportId != null && BASEBALL.getId().intValue() == sportId;
    }
    public static boolean isIceHockey(Integer sportId) {
        return sportId != null && ICE_HOCKEY.getId().intValue() == sportId;
    }
    public static boolean isAmericanFootball(Integer sportId) {
        return sportId != null && AMERICAN_FOOTBALL.getId().intValue() == sportId;
    }
    public static boolean isHandball(Integer sportId) {
        return sportId != null && HANDBALL.getId().intValue() == sportId;
    }
    public static boolean isBeachVolleyball(Integer sportId) {
        return sportId != null && BEACH_VOLLEYBALL.getId().intValue() == sportId;
    }
}
