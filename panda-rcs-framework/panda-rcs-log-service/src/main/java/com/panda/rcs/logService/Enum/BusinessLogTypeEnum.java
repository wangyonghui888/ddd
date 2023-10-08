package com.panda.rcs.logService.Enum;

import lombok.Getter;

/**
 * 风控日志操作类型
 */
@Getter
public enum BusinessLogTypeEnum {
    shtysz(1, "商户通用设置"),
    shdcxe(2, "商户单场限额"),
    yhdrxejms(3, "用户单日限额-旧限额模式"),
    yhdcxe(4, "用户单场限额"),
    dzdgxe(5, "单注单关限额"),
    dzcgxe(6, "单注串关限额"),
    dzcgxexb(7, "单注串关限额-新版"),
    yhdrxexms(8, "用户单日限额-新限额模式"),
    yhdrxeyl(9, "用户单日限额-盈利限额"),
    setchange(10, "商户藏单设置"),
    shgl(10010, "10010"),
    bqgl(10020, "10020"),
    fkcsgl(10030, "10030"),
    ddfkgl(10040, "10040"),
    yhtqjscsgz(10041, "10041"),
    ddblfz(10042, "10042"),
    qjkg(10043, "10043"),
    bqfkcs(10044, "10044"),
    plfz(10045, "10045"),
    wxlscgl(10050, "10050"),
    wxqdcgl(10060, "10060"),
    wbbzlsjl(10080, "10080"),
    ejbqbg(10000, "10000"),
    rjbq(10009,"二级标签"),
    rzkg(10092,"10092"),
    qlzskg(10093,"10093"),
    vrmrsz(10100, "10100"),
    vrplsz(10101, "10101"),
    vrlwplsz(10102, "10102"),
    vrdshbg(10103, "10103"),
    vrdshbgset(10104, "数据传输设置");
    private Integer type;
    private String value;
    BusinessLogTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getValue(Integer type) {
        for (BusinessLogTypeEnum businessLimitLogTypeEnum:values()){
            if (businessLimitLogTypeEnum.type.equals(type)){
                return businessLimitLogTypeEnum.value;
            }
        }
        return null;
    }
}
