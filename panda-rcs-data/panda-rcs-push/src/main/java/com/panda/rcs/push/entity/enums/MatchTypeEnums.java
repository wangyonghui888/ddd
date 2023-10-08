package com.panda.rcs.push.entity.enums;

public enum MatchTypeEnums {

    ATCH_TYPE_BASE(0, "常规赛事"),
    MATCH_TYPE_CHAMPION(1, "冠军赛事");

    private Integer key;

    private String value;

    MatchTypeEnums(Integer key, String value){
        this.key = key;
        this.value = value;
    }

    public Integer getKey(){
        return this.key;
    }

    public String getValue(){
        return this.value;
    }

}
