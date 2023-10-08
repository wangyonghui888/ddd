package com.panda.sport.rcs.constants;


import java.util.Arrays;
import java.util.List;

/**
 * 公用常量
 *
 * @author enzo
 */
public interface CommonConstants {

    /**
     * 角球对应玩法玩法
     */
    List<Long> cornerCategoryIds = Arrays.asList(111L, 113L, 114L, 115L, 116L, 117L, 118L, 226L, 227L, 119L, 121L, 122L, 123L, 124L, 228L, 229L);

    List<Long> cornerStatusCategoryIds = Arrays.asList(225L,111L,113L,114L,118L,125L,230L,231L,232L,233L,117L,119L,121L,122L,115L,116L,120L,123L,124L,112L,226L,227L,228L,229L);


    /**
     * 点球比分对应玩法
     */
    List<Long> kickCategoryIds = Arrays.asList(131L,132L,237L,134L,335L,238L,239L,240L,241L,133L,333L,334L);

    /**
     * 罚牌对应玩法
     */
    List<Long> cardCategoryIds = Arrays.asList(224L,138L,139L,140L,306L,307L,308L,309L,310L,311L,312L,313L,314L,315L,316L,317L,318L,319L,320L,321L,322L,323L);

    /**
     * 进球比分对应玩法
     */
    List<Long> goalCategoryIds = Arrays.asList(1L, 17L, 25L, 4L, 19L, 143L, 2L, 18L, 5L, 43L, 142L, 6L, 70L, 72L, 3L, 69L, 71L, 7L, 20L, 74L, 12L, 14L, 8L, 9L, 15L, 16L, 103L, 104L, 10L, 11L, 13L, 28L, 31L, 148L, 30L, 36L, 27L, 29L, 222L, 68L, 101L, 102L, 107L, 108L, 141L, 149L, 150L, 151L, 152L, 77L, 223L, 91L, 78L, 92L, 81L, 79L, 82L, 80L, 83L, 93L, 84L, 94L, 85L, 95L, 86L, 96L, 23L, 21L, 22L, 90L, 100L, 87L, 97L, 24L, 42L, 105L, 73L, 89L, 99L, 88L, 98L, 76L, 75L, 106L, 109L, 110L, 32L, 33L, 34L, 26L, 340L, 344L, 345L, 346L, 347L, 348L, 349L, 350L, 351L, 352L, 353L, 360L, 354L, 355L, 356L, 357L, 358L, 361L, 362L);


    //MQtopic

    String MATCH_LIVE_SET_TOPIC = "MONGODB_MATCH_SET";

    String WS_MARKET_PLACE_DATA_TOPIC = "rcs_predict_odds_placeNum_ws";

    String RCS_CLEAR_MARKET_CONFIG_TAG = "RCS_CLEAR_MARKET_CONFIG_TAG";

    String RCS_CLEAR_MARKET_CONFIG_TOPIC = "RCS_CLEAR_MARKET_CONFIG_TOPIC";

    String MATCH_CATEGORY_LIVE_UPDATE_TOPIC = "MONGODB_CATEGORY_LIVE";
    /**
     * 足球水差清零
     */
    String RCS_CLEAR_FOOTBALL_WATER_DIFF_TOPIC = "RCS_CLEAR_FOOTBALL_WATER_DIFF_TOPIC";

    String RCS_CLEAR_FOOTBALL_WATER_DIFF_TAG = "RCS_CLEAR_FOOTBALL_WATER_DIFF_TAG";
}
