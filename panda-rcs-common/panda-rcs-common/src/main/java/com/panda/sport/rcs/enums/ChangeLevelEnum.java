package com.panda.sport.rcs.enums;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  TODO
 * @Date: 2020-02-10 16:50
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum ChangeLevelEnum {
    MATCH(1, "赛事级别"),
    PLAY_STATE(2, "玩法阶段级别"),
    MARKET(3, "盘口级别"),
    PLAY(4, "玩法级别");
    private Integer level;
    private String value;

    ChangeLevelEnum(Integer level, String value) {
        this.level = level;
        this.value = value;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
