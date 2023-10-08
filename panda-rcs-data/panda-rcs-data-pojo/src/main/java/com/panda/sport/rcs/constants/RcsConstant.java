package com.panda.sport.rcs.constants;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.constants
 * @Description : 风控常量类
 * @Author : Paca
 * @Date : 2020-07-25 14:20
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsConstant {

    /**
     * 赛事ID
     */
    String MATCH_ID = "match_id_";

    /**
     * 玩法ID
     */
    String PLAY_ID = "play_id_";

    /**
     * 盘口ID
     */
    String MARKET_ID = "market_id_";

    String HOME_POSITION = "home";

    String AWAY_POSITION = "away";

    String HOME = "T1";

    String AWAY = "T2";

    /**
     * 默认盘口位置数量
     */
    int DEFAULT_MARKET_PLACE_AMOUNT = 10;

    /**
     * 特殊盘口值0.5
     */
    BigDecimal SPECIAL_MARKET_VALUE = new BigDecimal("0.5");

    /**
     * 默认spread
     */
    BigDecimal DEFAULT_SPREAD = new BigDecimal("0.4");

    /**
     * 滚球赛事状态
     */
    List<Integer> LIVE_MATCH_STATUS = Lists.newArrayList(1, 2, 10);

    /**
     * 基准分玩法
     */
    List<Long> BENCHMARK_SCORE = Lists.newArrayList(4L, 19L, 143L);

    /**
     * 大小盘
     */
    List<Long> TOTAL = Lists.newArrayList(2L, 10L, 11L, 18L, 26L, 34L, 38L, 45L, 51L, 57L, 63L, 87L, 88L, 97L, 98L, 114L, 115L, 116L, 122L, 123L, 124L, 127L, 134L, 145L, 146L, 156L, 157L, 164L, 169L, 173L, 177L, 182L, 186L, 198L, 199L, 202L, 220L, 221L, 233L, 244L, 245L, 246L, 250L, 251L, 252L, 254L, 257L, 258L, 262L, 263L, 264L);

    List<Long> OVER_X_UNDER = Lists.newArrayList(217L);

    List<Long> TOTAL_AND_OTHER = Lists.newArrayList(13L, 102L, 171L, 216L, 217L, 345L, 346L, 347L, 348L);

    /**
     * 让球/分赛果，三项盘，有盘口值，比 让球盘 多一个平局投注项
     */
    List<Long> HANDICAP_1X2 = Lists.newArrayList(3L, 69L, 71L);

    /**
     * 让球/分盘，都是两项盘，有盘口值
     */
    List<Long> HANDICAP = Lists.newArrayList(4L, 19L, 33L, 113L, 121L, 128L, 130L, 143L, 232L,
            19L, 39L, 46L, 52L, 58L, 64L, 143L);

    /**
     * 主要玩法-常规进球
     */
    List<Long> MAIN_GOAL = Lists.newArrayList(1L, 2L, 4L, 17L, 18L, 19L);

    /**
     * 主要玩法-常规角球
     */
    List<Long> MAIN_CORNER = Lists.newArrayList(111L, 113L, 114L, 119L, 121L, 122L);

    /**
     * 主要玩法-加时进球
     */
    List<Long> MAIN_OVERTIME_GOAL = Lists.newArrayList(126L, 127L, 128L, 129L, 130L);

    /**
     * 主要玩法-篮球-让分，全场/上半场/下半场/第一节/第二节/第三节/第四节
     */
    List<Long> MAIN_BASKETBALL_HANDICAP = Lists.newArrayList(39L, 19L, 143L, 46L, 52L, 58L, 64L);

    /**
     * 主要玩法-篮球-大小，全场/上半场/下半场/第一节/第二节/第三节/第四节
     */
    List<Long> MAIN_BASKETBALL_TOTAL = Lists.newArrayList(38L, 18L, 26L, 45L, 51L, 57L, 63L);

    List<Long> BASKETBALL_TOTAL = Lists.newArrayList(38L, 18L, 26L, 45L, 51L, 57L, 63L, 87L, 88L, 97L, 98L, 145L, 146L, 198L, 199L);

    /**
     * 主要玩法-篮球-单双，全场/上半场/下半场/第一节/第二节/第三节/第四节
     */
    List<Long> MAIN_BASKETBALL_ODD_EVEN = Lists.newArrayList(40L, 42L, 75L, 47L, 53L, 59L, 65L);

    /**
     * 净胜分
     */
    List<Long> WINNING_MARGIN = Lists.newArrayList(49L, 55L, 61L, 67L, 141L, 200L, 209L, 211L, 212L, 219L, 238L);

    /**
     * 进球球员
     */
    List<Long> GOALSCORER = Lists.newArrayList(35L, 36L, 148L, 150L, 151L, 152L);
}
