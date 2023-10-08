package com.panda.rcs.push.entity.enums;

public enum StatusEnum {

    //赛事盘口状态
    MATCH_ACTIVE(0, "开盘"),

    MATCH_SUSPENDED(1, "封盘"),

    MATCH_DEACTIVATED(2, "关盘"),

    MATCH_LOCK(11, "锁盘"),

    //盘口状态
    HANDICAP_ACTIVE(0, "开盘"),

    HANDICAP_SUSPENDED(1, "封盘"),

    HANDICAP_DEACTIVATED(2, "关盘"),

    HANDICAP_SETTLED(3, "已结算"),

    HANDICAP_CANCELLED(4, "已取消"),

    HANDICAP_HANDED_OVER(5, "已移交"),

    HANDICAP_LOCK(11, "锁盘"),

    //投注项状态
    ODDS_ACTIVE(1, "开盘"),

    ODDS_SUSPENDED(2, "封盘"),

    ODDS_DEACTIVATED(3, "关盘"),

    ODDS_LOCK(4, "锁盘");

    private Integer key;

    private String value;

    StatusEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
