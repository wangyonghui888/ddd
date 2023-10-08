package com.panda.rcs.stray.limit.entity.enums;

import java.util.HashSet;
import java.util.Set;

public enum SportEnum {

    /**
     * 足球
     */
    SPORT_FOOTBALL(1, "足球"),

    /**
     * 篮球
     */
    SPORT_BASKETBALL(2, "篮球"),

    /**
     * 棒球
     */
    SPORT_BASEBALL(3, "棒球"),

    /**
     * 冰球
     */
    SPORT_ICEHOCKEY(4, "冰球"),

    /**
     * 网球
     */
    SPORT_TENNIS(5, "网球"),

    /**
     * 美式足球
     */
    SPORT_USEFOOTBALL(6, "美式足球"),

    /**
     * 斯诺克
     */
    SPORT_SNOOKER(7, "斯诺克"),

    /**
     * 乒乓球
     */
    SPORT_PINGPONG(8, "乒乓球"),

    /**
     * 排球
     */
    SPORT_VOLLEYBALL(9, "排球"),

    /**
     * 羽毛球
     */
    SPORT_BADMINTON(10, "羽毛球"),

    /**
     * 手球
     */
    SPORT_HANDBALL(11, "手球"),

    /**
     * 拳击
     */
    SPORT_BOXING(12, "拳击/MMA"),

    /**
     * 沙滩排球
     */
    SPORT_BEACH_VOLLEYBALL(13, "沙滩排球"),

    /**
     * 英式橄榄球 Rugby
     */
    SPORT_ENGLAND_RUGBY_BALL(14, "英式橄榄球"),

    /**
     * 曲棍球 hockey
     */
    SPORT_HOCKEY_BALL(15, "曲棍球"),

    /**
     * 水球 water polo
     */
    SPORT_WATER_POLO(16, "水球"),

    /**
     * 足球
     */
    VIRTUAL_SPORT_FOOTBALL(1001, "虚拟足球"),

    VIRTUAL_SPORT_BASKETBALL(1004, "虚拟蓝球")

    ;

    private Integer key;

    private String value;

    /**
     * 获取运动类型
     *
     * @param sportId
     * @return
     */
    public static SportEnum getSportEnum(Integer sportId) {
        for (SportEnum e : values()) {
            if (e.key.equals(sportId)) return e;
        }
        return null;
    }


    SportEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }


    private static Set<Integer> sportSet = new HashSet<>();

    public static Boolean contains(Integer sportId) {
        return sportSet.contains(sportId);
    }

    static {
        for (SportEnum e : values()) {
            sportSet.add(e.getKey());
        }
    }

    /**
     * 根据key查询具体球种枚举
     * @param key 球种Id
     * @return 返回当前枚举
     */
    public static SportEnum getValue(Integer key) {
        SportEnum [] allSportArr = SportEnum.values();
        for (SportEnum em : allSportArr) {
            if (em.getKey().equals(key)) {
                return em;
            }
        }
        return null;
    }

}
