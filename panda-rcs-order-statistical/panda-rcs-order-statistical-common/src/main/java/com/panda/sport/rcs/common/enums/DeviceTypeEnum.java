package com.panda.sport.rcs.common.enums;

/**
 * 设备类型
 * @author derre
 * @date 2022-03-29
 */
public enum DeviceTypeEnum {

    H5(1, "H5"),
    PC(2, "PC"),
    Android(3, "Android"),
    IOS(4, "IOS")
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
    DeviceTypeEnum(Integer code, String name) {
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
    public static DeviceTypeEnum get(Integer betType) {
        for (DeviceTypeEnum betTypeEnum : DeviceTypeEnum.values()) {
            if (betTypeEnum.code.equals(betType)) {
                return betTypeEnum;
            }
        }

        return null;
    }

}
