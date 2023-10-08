package com.panda.rcs.stray.limit.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RcsMerchantHighRiskRespVo {


    @ApiModelProperty("高风险单注赔付限额")
    List<RcsMerchantHighRiskLimit> rcsMerchantHighRiskLimits;

    /**
     * 操作人IP
     */
    private String ip;




}
