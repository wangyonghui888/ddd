package com.panda.sport.rcs.enums;

/**
 * @author :  kimi
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.constants
 * @Description :  投注阶段枚举
 * @Date: 2019-10-12 11:32
 */
public enum MatchTypeEnum {
    //
    NO_KICK_OFF(0, "未开赛"),

    ROLLING_BALL(1, "滚球");

    private Integer id;
    private String name;

    private MatchTypeEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static MatchTypeEnum getMatchTypeEnum(int id) {
        for (MatchTypeEnum matchTypeEnum : values()) {
            if (matchTypeEnum.getId() == id) {
                return matchTypeEnum;
            }
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
