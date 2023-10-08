package com.panda.sport.rcs.predict.common;

/**
 * forecast玩法id
 */
public class ForecastPlayIds {
    //让球
    public static Integer letPoint[] = new Integer[]{4, 19, 113, 121, 128, 130, 306, 308,334};
    //大小
    public static Integer bigSmall[] = new Integer[]{2, 18, 114, 122, 127, 332, 307, 309,335};
    //独赢
    public static Integer aloneWin[] = new Integer[]{1, 17, 111, 119, 126, 129, 310, 311,333};//,

    public static final String RCS_EVENT_TO_ORDER_TOPIC="RCS_EVENT_TO_ORDER_TOPIC";
}
