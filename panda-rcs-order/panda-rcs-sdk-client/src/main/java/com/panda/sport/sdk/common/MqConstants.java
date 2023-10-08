package com.panda.sport.sdk.common;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.common
 * @Description :  TODO
 * @Date: 2019-11-01 15:50
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class MqConstants {
    /**
     * 赛事盘口数据
     **/
    public static final String MQ_STANDARD_MATCH_INFO_STATE_TOPIC = "PANDA_RCS_STATE";
    /**
     * 赛事盘口数据
     **/
    public static final String MQ_STANDARD_MATCH_INFO_STATE_TOG = "STATE";

    public static final String ORDER_AMOUNT_CHANGE_TOPIC = "ORDER_AMOUNT_CHANGE_TOPIC";


    /******************************赛事实时数据同步start***************************************/

    /**
     * 赛事状态推送
     **/
    public static final String REALTIME_SYNC_STATUS_TOPIC = "REALTIME_SYNC_DATA";
    public static final String REALTIME_SYNC_STATUS_TAG = "REALTIME_SYNC_STATUS_TAG";

    /**
     * 赛事结果推送
     **/
    public static final String REALTIME_SYNC_RESULT_TOPIC = REALTIME_SYNC_STATUS_TOPIC;
    public static final String REALTIME_SYNC_RESULT_TAG = "REALTIME_SYNC_RESULT_TAG";

    /**
     * 赛事事件推送
     **/
    public static final String REALTIME_SYNC_EVENT_TOPIC = REALTIME_SYNC_STATUS_TOPIC;
    public static final String REALTIME_SYNC_EVENT_TAG = "REALTIME_SYNC_EVENT_TAG";

    /**
     * 赛事赔率推送
     **/
    public static final String REALTIME_SYNC_MARKET_ODDS_TOPIC = "STANDARD_MARKET_ODDS";
    public static final String REALTIME_SYNC_MARKET_ODDS_TAG = "";

    /**
     * 盘口开售推送
     **/
    public static final String MATCH_ADVANCE_SALE = "Match_Advance_Sale";

    /**
     * 平衡值推送
     **/
    public static final String REALTIME_SYNC_BALANCE_TOPIC = "REALTIME_SYNC_BALANCE_TOPIC";
    public static final String REALTIME_SYNC_BALANCE_TAG = "REALTIME_SYNC_BALANCE_TAG";

    /**
     * 玩法阶段自动手动推送
     **/
    public static final String DATA_SOURCE_DURING_GAME_PLAY_TOPIC = "DATA_SOURCE_DURING_GAME_PLAY_TOPIC";
    public static final String DATA_SOURCE_DURING_GAME_PLAY_TAG = "DATA_SOURCE_DURING_GAME_PLAY_TAG";

    /**
     * 融合事件推送
     **/
    public static final String MATCH_EVENT_INFO_TOPIC = "MATCH_EVENT_INFO";
    public static final String MATCH_EVENT_INFO_TO_RISK = "MATCH_EVENT_INFO_TO_RISK";
    /**
     * 融合状态推送
     **/
    public static final String STANDARD_MATCH_STATUS = "STANDARD_MATCH_STATUS";
    /**
     * 融合赛果推送
     **/
    public static final String STANDARD_MATCH_RESULT = "STANDARD_MATCH_RESULT";
    /******************************赛事实时数据同步end***************************************/

    /******************************websocket队列数据start***********************************/

    /**
     * 实时更新实货量、期望值
     **/
    public static final String WS_ODDS_CHANGED_TOPIC = "WS_CHANNEL_TOPIC";
    public static final String WS_ODDS_CHANGED_TAG = "WS_ODDS_CHANGED_TAG";


    /**
     * @Description 期望详情
     * @Param
     * @Author myname
     * @Date 15:03 2020/1/11
     * @return
     **/
    public static final String WS_SCROLL_BALL_LIVE_BET_DETAIL_BY_PLAYID_2_4_18_19_CHANGED_TAG = "WS_SCROLL_BALL_LIVE_BET_DETAIL_BY_PLAYID_2_4_18_19_CHANGED_TAG";

    /**
     * @Description 玩法12和15
     * @Param
     * @Author myname
     * @Date 16:16 2020/1/13
     * @return
     **/
    public static final String WS_SCROLL_BALL_LIVE_BET_DETAIL_BY_PLAYID_12_15_CHANGED_TAG = "WS_SCROLL_BALL_LIVE_BET_DETAIL_BY_PLAYID_12_15_CHANGED_TAG";
    /**
     * 同联赛赛事 实时更新实货量
     **/
    public static final String WS_MATCH_BET_CHANGED_TOPIC = WS_ODDS_CHANGED_TOPIC;
    public static final String WS_MATCH_BET_CHANGED_TAG = "WS_MATCH_BET_CHANGED_TAG";

    /**
     * 注单状态实时更新
     **/
    public static final String WS_ORDER_BET_RECORD_TOPIC = WS_ODDS_CHANGED_TOPIC;
    public static final String WS_ORDER_BET_RECORD_TAG = "WS_ORDER_BET_RECORD_TAG";

    /******************************websocket队列数据end***********************************/

    /******************************websocket队列路由数据start***********************************/

    public static final String WS_SUBSCRIBE_TOPIC = "RCS_WS_SUBSCRIBE_TOPIC";

    public static final String RCS_WS_GET_WAY_TOPIC = "RCS_WS_GET_WAY_TOPIC";

    /******************************websocket队列路由数据end***********************************/

    /**
     * 待计算注单
     **/
    public static String UNCALC_SETTLE_BATCH_TOPIC = "UNCALC_SETTLE_BATCH_TOPIC";
    public static String SETTLE_STATISTIC_TAG = "SETTLE_STATISTIC_TAG";

    public static final String MATCH_PERIOD_CHANGE = "MATCH_PERIOD_CHANGE";
    public static final String MARKET_CONGIG_UPDTAE_TOPIC = "MARKET_CONGIG_UPDTAE_TOPIC";
    public static final String MARKET_WATER_CONFIG_TOPIC = "MARKET_WATER_CONFIG_TOPIC";

}
