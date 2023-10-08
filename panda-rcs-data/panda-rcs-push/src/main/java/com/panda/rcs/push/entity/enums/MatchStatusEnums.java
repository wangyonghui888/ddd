package com.panda.rcs.push.entity.enums;

public enum MatchStatusEnums {

    MATCH_STATUS_LIVE(1, "滚球状态");

    private Integer key;

    private String value;

    MatchStatusEnums(Integer key, String value){
        this.key = key;
        this.value = value;
    }

    public Integer getKey(){
        return this.key;
    }

    public String getValue(){
        return this.value;
    }

    public static com.panda.rcs.push.entity.enums.MatchStatusEnums getMatchStatusEnums(Integer matchStatus) {
        com.panda.rcs.push.entity.enums.MatchStatusEnums[] allMatchStatusArr = com.panda.rcs.push.entity.enums.MatchStatusEnums.values();
        for (com.panda.rcs.push.entity.enums.MatchStatusEnums ms : allMatchStatusArr) {
            if (ms.getKey().equals(matchStatus)) {
                return ms;
            }
        }
        return null;
    }
}
