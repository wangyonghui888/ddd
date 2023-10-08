package com.panda.sport.rcs.common.enums;

/**
 * 赛事类型
 * @author derre
 * @date 2022-03-29
 */
public enum MatchTypeEnum {
    EARLY(1, "早盘赛事"),
    ROLL(2, "滚球盘赛事"),
    CHAMPION(3, "冠军盘赛事"),
    ACTIVITY(4, "活动赛事")
    ;


    /**
     * 名称
     */
    private String name;
    /**
     * 编码
     */
    private Integer code;

    /**
     * 初始化
     *
     * @param code
     * @param name
     */
    MatchTypeEnum(Integer code, String name) {
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
     * @param betType
     */
    public static MatchTypeEnum get(Integer betType) {
        for (MatchTypeEnum betTypeEnum : MatchTypeEnum.values()) {
            if (betTypeEnum.code.equals(betType)) {
                return betTypeEnum;
            }
        }

        return null;
    }

}
