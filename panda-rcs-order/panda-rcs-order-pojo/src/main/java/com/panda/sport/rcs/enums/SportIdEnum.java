package com.panda.sport.rcs.enums;

import com.google.common.collect.Lists;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.List;

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

    FOOTBALL(1L, "足球",Lists.newArrayList()),
    BASKETBALL(2L, "篮球",Lists.newArrayList()),
    BASEBALL(3L, "棒球", RcsConstant.BASEBALL_MY_PLAYS),
    ICE_HOCKEY(4L, "冰球",RcsConstant.ICE_HOCKEY_MY_PLAYS),
    TENNIS(5L, "网球",Lists.newArrayList()),
    AMERICAN_FOOTBALL(6L, "美式足球",RcsConstant.AMERICAN_FOOTBALL_MY_PLAYS),
    SNOOKER(7L, "斯洛克",Lists.newArrayList()),
    PING_PONG(8L, "乒乓球",Lists.newArrayList()),
    VOLLEYBALL(9L, "排球",Lists.newArrayList()),
    BADMINTON(10L, "羽毛球",RcsConstant.BADMINTON_MY_PLAYS),
    HANDBALL(11L, "手球",RcsConstant.HANDBALL_MY_PLAYS),
    BOXING(12L, "拳击",RcsConstant.BOXING_MY_PLAYS),
    BEACH_VOLLEYBALL(13L, "沙滩排球",RcsConstant.BEACH_VOLLEYBALL_MY_PLAYS),
    RUGBY_UNION(14L, "联合式橄榄球",RcsConstant.RUGBY_UNION_MY_PLAYS),
    HOCKEY(15L, "曲棍球",RcsConstant.HOCKEY_MY_PLAYS),
    WATER_POLO(16L, "水球",RcsConstant.WATER_POLO_MY_PLAYS);

    private Long id;


    private String name;
    private List<Integer> myPlays;


    public List<Integer> getPlays(){
        return this.myPlays;
    }
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
    public static boolean isBaseBall(Long sportId) {
        return BASEBALL.getId().equals(sportId);
    }
    public static boolean isBadminton(Long sportId) {
        return BADMINTON.getId().equals(sportId);
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
    public static boolean isRugbyUnion(Integer sportId) {
        return sportId != null && RUGBY_UNION.getId().intValue() == sportId;
    }
    public static boolean isHockey(Integer sportId) {
        return sportId != null && HOCKEY.getId().intValue() == sportId;
    }
    public static boolean isWaterOolo(Integer sportId) {
        return sportId != null && WATER_POLO.getId().intValue() == sportId;
    }
    public static boolean isBoxing(Integer sportId) {
        return sportId != null && BOXING.getId().intValue() == sportId;
    }

    public static List<Integer> getMyPlaysBySportId(Long sportId) {
        for (SportIdEnum ele : values()) {
            if(ele.getId().longValue() == sportId.longValue()) {
                return ele.getPlays();
            }
        }
        return Lists.newArrayList();
    }

    public static String getNameById(Long sportId) {
        for (SportIdEnum ele : values()) {
            if(ele.getId().longValue() == sportId.longValue()) {
                return ele.name;
            }
        }
        return "其他";
    }
}
