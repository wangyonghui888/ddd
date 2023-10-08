package com.panda.sport.rcs.pojo.vo;

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
     * 商户code
     */
    private String businessCode;

    /**
     * mts折扣
     */
    private BigDecimal mtsRate;
    private String mtsRateStr;

    /**
     * mts折扣通用
     */
    private BigDecimal mtsRateAll;

    /**
     * 虚拟折扣
     */
    private BigDecimal virtualRate;
    private String virtualRateStr;

    /**
     * VR藏单状态(1--关闭 2--开启)
     */
    private Integer vrEnable;
    private String vrEnableStr;

    private Integer vrEnableAll;

    /**
     * 虚拟折扣通用
     */
    private BigDecimal virtualRateAll;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 商户id集合
     */
    private List<String> businessIds;

    /**
     * 商户id集合，逗号隔开的字符串
     */
    private String busIds;

    private BigDecimal ctsRate;
    private String ctsRateStr;
    private BigDecimal gtsRate;
    private String gtsRateStr;
    private BigDecimal otsRate;
    private String otsRateStr;
    private BigDecimal rtsRate;
    private String rtsRateStr;

    private BigDecimal ctsRateAll;
    private BigDecimal gtsRateAll;
    private BigDecimal otsRateAll;
    private BigDecimal rtsRateAll;
}
