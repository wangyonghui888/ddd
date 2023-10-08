package com.panda.sport.rcs.vo;

import lombok.Data;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-02-2022/2/13 19:13
 */
@Data
public class ConfigCashOutVo extends ConfigCashOutTradeItemVo{

    /**
     * 数据商是否支持提前结算
     * ＆关系，现存盘口只要一个盘口满足
     */
    private Integer cashOutStatus;

    /**
     * 前端页面obt按钮状态 0:关 1:开
     */
    private Integer obtStatus;
    /**
     * 前端页面mbt按钮状态 0:关 1:开
     */
    private Integer mbtStatus;
}
