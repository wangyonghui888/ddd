package com.panda.rcs.push.entity.constant;

import com.panda.rcs.push.entity.enums.SportEnum;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础常量类
 */
public class BaseConstant {

    /**
     * 分隔下划线
     */
    public static final String SEPARATE_UNDERSCORE = "_";

    public static final String NULL_STRING = "";

    public static final String COMMA = ",";

    public static final String STAR = "*";

    public static final String PREFIX_AMOUNT = "TOA";

    public static final String CACHE_PLAY_SET = "CACHE_PLAY_SET";

    /**
     * 操盘球种
     */
    public static final List<Integer> TRADER_SPORT_LISTS = new ArrayList<Integer>(){{
        this.add(SportEnum.SPORT_FOOTBALL.getKey()); //足球
        this.add(SportEnum.SPORT_BASKETBALL.getKey()); //篮球
        this.add(SportEnum.SPORT_TENNIS.getKey()); //网球
        this.add(SportEnum.SPORT_SNOOKER.getKey()); //斯洛克
        this.add(SportEnum.SPORT_PINGPONG.getKey()); //乒乓球
        this.add(SportEnum.SPORT_VOLLEYBALL.getKey()); //排球
        this.add(SportEnum.SPORT_BASEBALL.getKey()); //棒球
        this.add(SportEnum.SPORT_BADMINTON.getKey()); //羽毛球
        this.add(SportEnum.SPORT_ICEHOCKEY.getKey()); //冰球
    }};

    /**
     * 综合货量球种
     */
    public static final List<Integer> AMOUNT_SPORT_LISTS = new ArrayList<Integer>(){{
        this.add(SportEnum.SPORT_BASKETBALL.getKey()); //篮球
        this.add(SportEnum.SPORT_TENNIS.getKey()); //网球
        this.add(SportEnum.SPORT_SNOOKER.getKey()); //斯洛克
        this.add(SportEnum.SPORT_PINGPONG.getKey()); //乒乓球
        this.add(SportEnum.SPORT_VOLLEYBALL.getKey()); //排球
        this.add(SportEnum.SPORT_BASEBALL.getKey());//棒球
        this.add(SportEnum.SPORT_ICEHOCKEY.getKey());//冰球
        this.add(SportEnum.SPORT_BADMINTON.getKey());//羽毛球
    }};

    /**
     * 特殊订阅指令处理
     */
    public static final List<Integer> SPECIAL_SUB_lISTS = new ArrayList<Integer>(){{
        this.add(SubscriptionEnums.MATCH_ODDS.getKey());
        this.add(SubscriptionEnums.MATCH_CLEANUP_LINK_DATA.getKey());
        this.add(SubscriptionEnums.MATCH_TRADER_STATUS.getKey());
        this.add(SubscriptionEnums.LIVE_ODDS_UOF_DATA.getKey());
    }};

}
