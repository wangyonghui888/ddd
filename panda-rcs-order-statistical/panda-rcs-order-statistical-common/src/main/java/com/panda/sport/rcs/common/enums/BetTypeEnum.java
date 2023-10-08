package com.panda.sport.rcs.common.enums;

/**
 * @author :  toney
 * @Project Name :  panda-rcs-order-statistical
 * @Package Name :  com.panda.sport.rcs.common.enums
 * @Description :  投注类型
 * @Date: 2021-01-12 11:59
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum BetTypeEnum {
    /**
     * 单关
     */
    SINGLE(1, "单关"),
    /**
     * 复式
     */
    DUPLEX(2, "串关"),
    /**
     * 冠军
     */
    CHAMPION(3, "冠军");


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
    BetTypeEnum(Integer code, String name) {
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
    public static BetTypeEnum get(Integer betType) {
        for (BetTypeEnum betTypeEnum : BetTypeEnum.values()) {
            if (betTypeEnum.code.equals(betType)) {
                return betTypeEnum;
            }
        }

        return null;
    }
}
