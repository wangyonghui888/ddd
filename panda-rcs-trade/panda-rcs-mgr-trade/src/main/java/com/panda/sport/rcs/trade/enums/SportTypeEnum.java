package com.panda.sport.rcs.trade.enums;

public enum SportTypeEnum {
    FOOTBALL(1,"足球"),
    BASKETBALL(2,"篮球"),
    BASEBALL(3,"棒球"),
    ICE_HOCKEY(4,"冰球"),
    TENNIS(5,"网球"),
    AMERICAN_FOOTBALL(6,"美式足球"),
    SNOOKER(7,"斯诺克"),
    TABLE_TENNIS(8,"兵兵球"),
    VOLLEYBALL(9,"排球")
    ;
    private Integer sportId;
    private String name;

    SportTypeEnum(Integer sportId, String name) {
        this.sportId = sportId;
        this.name = name;
    }
    public static String getValue(Integer sportId) {
        for (SportTypeEnum sportTypeEnum:values()){
            if (sportTypeEnum.sportId.equals(sportId)){
                return sportTypeEnum.name;
            }
        }
        return null;
    }
    public Integer getSportId() {
        return sportId;
    }

    public String getName() {
        return name;
    }
}
