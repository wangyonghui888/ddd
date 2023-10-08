package com.panda.sport.rcs.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RcsQuotaBusinessRateApiDTO {

    /**
     * 商户id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long businessId;

    /**
     * 商户code
     */
    private String businessCode;
    /**
     * VR藏单状态(1--关闭 2--开启)
     */
    private Integer vrEnable;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;
}
