package com.panda.rcs.order.entity.enums;

public enum  SubscriptionEnums {

    CMD_HEARTBEAT_300(300, "连接心跳"),
    MATCH_STATUS(30001, "赛事状态"),
    MATCH_EVENT(30003, "赛事事件"),
    MATCH_ODDS(30005, "赛事标准赔率推送"),
    TIMELY_ORDER(30006, "及时注单推送"),
    MATCH_CLEANUP_LINK_DATA(30007, "平衡值/盘口关联/自动水差"),
    MATCH_TRADER_STATUS(30012, "操盘相关状态推送"),
    THIRD_MATCH_EVENT(30013, "三方事件流"),
    LIVE_ODDS_UOF_DATA(30014, "UOF统计数据"),
    EXCEED_FIFTEEN_ODDS(30015, "赔率超过一分钟未下发赔率消息"),
    MATCH_OTHER_STATUS(30022, "推送赛事级别以下状态"),
    UPDATE_MARKET_INDEX(30033, "更新盘口索引"),
    MARKET_PLACE_BET_AMOUNT(30035, "盘口位置货量推送"),
    FOOTBALL_FORECAST(30036, "货量forecast推送"),
    MESSAGE_NOT_READ(30039, "消息未读数量"),
    TRADER_MESSAGE(30040, "操盘消息"),
    PLAY_CONFIGURATION(30041, "玩法配置推送"),
    NO_FOOTBALL_AMOUNT(30042, "非足球货量推送"),
    MATCH_TRADE_MEMO_REMIND(30086,"操盘备注读取信息推送"),
    MARKET_PREDICT_FORECAST(30044, "盘口货量Forecast"),
    PLACE_PREDICT_FORECAST(30045, "坑位货量Forecast"),
    SELF_MATCH_EVENT_SOURCE(30051, "自研播报板"),
    PLAY_SHOW_STATUS(30061, "玩法配置推送"),
    SERVER_ANSWER_MESSAGE(30099, "订阅应答-无需处理其他业务"),
    USER_LABEL_CHANGE(50001, "用户标签变更"),
    RCS_MULTIPLE_ODDS(50005, "百家赔率"),
    PRE_RESULT_MARKET(60001, "提前结算标识"),
    GO_MARKET_PREDICT_FORECAST(30944, "Go版本盘口货量Forecast"),
    GO_FOOTBALL_FORECAST(30936, "Go版本货量forecast推送"),
    GO_PLACE_PREDICT_FORECAST(30945, "坑位货量Forecast");

    private Integer key;

    private String value;

    SubscriptionEnums(Integer key, String value){
        this.key = key;
        this.value = value;
    }

    public Integer getKey(){
        return this.key;
    }

    public String getValue(){
        return this.value;
    }

    /**
     * 根据key查询具体指令
     * @param key 指令Id
     * @return 返回当前枚举
     */
    public static SubscriptionEnums getSubscriptionEnums(Integer key) {
        SubscriptionEnums[] allSubArr = SubscriptionEnums.values();
        for (SubscriptionEnums em : allSubArr) {
            if (em.getKey().equals(key)) {
                return em;
            }
        }
        return null;
    }

}
