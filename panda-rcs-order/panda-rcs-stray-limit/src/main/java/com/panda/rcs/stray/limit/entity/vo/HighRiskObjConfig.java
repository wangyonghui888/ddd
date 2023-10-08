package com.panda.rcs.stray.limit.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HighRiskObjConfig {

    @ApiModelProperty("区间最大值")
    private BigDecimal max;


    @ApiModelProperty("区间最小值")
    private BigDecimal min;

}