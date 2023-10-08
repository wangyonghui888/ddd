package com.panda.sport.rcs.virtual.third.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RcsQuotaBusinessRateDTO {

    /**
     * 商户id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long businessId;

    /**
     * 虚拟折扣
     */
    private BigDecimal virtualRate;

    /**
     * VR藏单状态(1--关闭 2--开启)
     */
    private Integer vrEnable;
}
