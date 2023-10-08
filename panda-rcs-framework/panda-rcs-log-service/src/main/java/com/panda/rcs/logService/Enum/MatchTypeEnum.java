package com.panda.rcs.logService.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author :  toney
 * @Project Name :  panda-rcs-trade
 * @Package Name :  com.panda.sport.rcs.trade.enums
 * @Description :  matchType
 * @Date: 2020-08-05 10:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Getter
@AllArgsConstructor
public enum MatchTypeEnum {
    LIVE(0, "live", "滚球"),
    EARLY(1, "pre", "早盘"),
    NONE(-1, "no", "未知"),
    OPERATE_PAGE_ZPCP(14, "OPERATE_PAGE_ZPCP", "早盘操盘"),
    OPERATE_PAGE_GQCP(17, "OPERATE_PAGE_GQCP", "滚球操盘"),
    OPERATE_PAGE_ZPCP_SZ(110, "OPERATE_PAGE_ZPCP_SZ", "早盘操盘-设置"),
    OPERATE_PAGE_ZPSS(13, "OPERATE_PAGE_ZPSS", "早盘赛事"),
    OPERATE_PAGE_ZPCP_CYWF(15, "OPERATE_PAGE_ZPCP_CYWF", "早盘操盘-次要玩法"),
    OPERATE_PAGE_ZPCP_TJCK(100, "OPERATE_PAGE_ZPCP_TJCK", "早盘操盘-调价窗口"),
    OPERATE_PAGE_ZPCP_CYWF_TJCK(101, "OPERATE_PAGE_ZPCP_CYWF_TJCK", "早盘操盘-次要玩法-调价窗口"),
    OPERATE_PAGE_GQSS(16, "OPERATE_PAGE_GQSS", "滚球赛事"),
    OPERATE_PAGE_GQCP_CYWF(18, "OPERATE_PAGE_GQCP_CYWF", "滚球操盘-次要玩法"),
    OPERATE_PAGE_GQCP_TJCK(102, "OPERATE_PAGE_GQCP_TJCK", "滚球操盘-调价窗口"),
    OPERATE_PAGE_GQCP_CYWF_TJCK(103, "OPERATE_PAGE_GQCP_CYWF_TJCK", "滚球操盘-次要玩法-调价窗口"),
    OPERATE_PAGE_LSCSSZ(21, "OPERATE_PAGE_LSCSSZ", "联赛参数设置"),
    OPERATE_PAGE_WFJGL_ZQ(120, "OPERATE_PAGE_WFJGL_ZQ", "玩法集管理-足球"),
    AO_FB(112, "AO FB", "AO FB"),
    OPERATE_PAGE_WFJGL_LQ(121, "玩法集管理-篮球", "玩法集管理-篮球"),
    OPERATE_PAGE_GQCP_SZ(111, "OPERATE_PAGE_GQCP_SZ", "滚球操盘-设置");


    private Integer id;
    private String code;
    private String name;

    public static String getNameById(Integer value) {
        MatchTypeEnum[] businessModeEnums = values();
        for (MatchTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getId().equals(value)) {
                return businessModeEnum.getName();
            }
        }
        return null;
    }




}
