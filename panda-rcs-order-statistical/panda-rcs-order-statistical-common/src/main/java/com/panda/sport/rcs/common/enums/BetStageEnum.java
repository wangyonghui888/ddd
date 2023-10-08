package com.panda.sport.rcs.common.enums;

/**
 * @author :  toney
 * @Project Name :  panda-rcs-order-statistical
 * @Package Name :  com.panda.sport.rcs.common.enums
 * @Description :  投注阶段
 * @Date: 2021-01-12 12:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum BetStageEnum {
    /**
     * 早盘
     */
    EARLY(1,"早盘"),
    /**
     * 滚球
     */
    LIVE(2,"滚球"),

    CHAMPION(3,"冠军"),

    Virtual(4,"虚拟赛事");

    ;
    /**
     * 名称
     */
    private String name;
    /**
     * 编码
     */
    private Integer code;

    BetStageEnum(Integer code,String name) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取投注类型名称
     *
     * @param betStage
     */
    public static BetStageEnum get(Integer betStage) {
        for (BetStageEnum betStageEnum : BetStageEnum.values()) {
            if (betStageEnum.code.equals(betStage)) {
                return betStageEnum;
            }
        }

        return null;
    }
}
