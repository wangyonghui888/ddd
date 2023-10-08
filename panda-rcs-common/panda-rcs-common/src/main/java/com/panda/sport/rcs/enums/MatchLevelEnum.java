package com.panda.sport.rcs.enums;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.enums
 * @Description :  TODO
 * @Date: 2019-11-08 20:46
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum MatchLevelEnum {
    //联赛
    TOURNAMENT(2),
    //赛事
    MATCH(3),
    //盘口
    MATCH_MARKET(4),
    //玩法
    PLAY(1);
    int level;

    private MatchLevelEnum(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
