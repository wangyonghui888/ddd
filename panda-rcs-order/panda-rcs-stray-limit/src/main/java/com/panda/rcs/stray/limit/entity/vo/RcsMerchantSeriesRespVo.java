package com.panda.rcs.stray.limit.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RcsMerchantSeriesRespVo {

    @ApiModelProperty("单日串关赔付总限额及单日派彩总限额")
    private RcsMerchantSeriesConfig rcsMerchantSeriesConfig;


    @ApiModelProperty("单日串关赛种赔付限额及派彩限额")
    private List<RcsMerchantSportLimit> rcsMerchantSportLimitList;


    @ApiModelProperty("单日串关类型赔付总限额")
    private List<RcsMerchantLimitCompensation> rcsMerchantLimitCompensationList;


    @ApiModelProperty("单日串关赛种赔付限额及派彩限额")
    private RcsMerchantSportLimit rcsMerchantSportLimit;


    @ApiModelProperty("单日串关类型赔付总限额")
    private RcsMerchantLimitCompensation rcsMerchantLimitCompensation;

    /**
     * 操作人IP
     */
    private String ip;
}
