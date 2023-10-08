package com.panda.sport.rcs.third.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Beulah
 * @date 2023/3/31 17:04
 * @description todo
 */
@Getter
@AllArgsConstructor
public enum ThirdIsCacheEnum {

    //mtsIsCache 和业务约定 该gts订单是否走的缓存 0 mts  1 mts缓存接单 2 mts-PA接单 5 gts  6 gts缓存接单 7gts-PA接单

    //MTS
    MTS(0, "mts"),
    MTS_CACHE(1, "mts-0缓存接单"),
    MTS_PA(2, " mts-PA接单"),
    MTS_1(3, " mts-1接单"),
    PA_0(4, " PA_0接单"),

    //GTS
    GTS(5, "gts"),
    GTS_CACHE(6, "gts缓存接单"),
    GTS_PA(7, "gts-PA接单"),

    //特殊事件
    PA_1(9, "PK-1"),
    PA_2(10, "单刀-2"),
    PA_3(11, "危险任意球-3"),
    PA_4(12, "最后几分钟危险球-4"),

    //BTS
    BE(8, "bts接单"),
    BE_CACHE(13, "bts缓存接单"),
    BE_PA(14, "bts-pa接单"),

    //CTS
    BC(15, "cts接单"),
    BC_CACHE(16, "cts缓存接单"),
    BC_PA(17, "cts-pa接单"),

    //OD
    OD(21, "oddin接单"),
    OD_CACHE(22, "oddin接单缓存接单"),
    OD_PA(23, "oddin接单-pa接单");
    private int code;
    private String text;
}
