package com.panda.rcs.stray.limit.entity.enums;

/**
 * 风控日志操作类型
 */
public enum BusinessLimitLogTypeEnum {
    shtysz(1, "商户通用设置"),
    shdcxe(2, "商户单场限额"),
    yhdrxejms(3, "用户单日限额-旧限额模式"),
    yhdcxe(4, "用户单场限额"),
    dzdgxe(5, "单注单关限额"),
    dzcgxe(6, "单注串关限额"),
    dzcgxexb(7, "单注串关限额-新版"),
    yhdrxexms(8, "用户单日限额-新限额模式"),
    yhdrxeyl(9, "用户单日限额-盈利限额");
    private Integer type;
    private String value;
    BusinessLimitLogTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getValue(Integer type) {
        for (BusinessLimitLogTypeEnum businessLimitLogTypeEnum:values()){
            if (businessLimitLogTypeEnum.type.equals(type)){
                return businessLimitLogTypeEnum.value;
            }
        }
        return null;
    }
}
