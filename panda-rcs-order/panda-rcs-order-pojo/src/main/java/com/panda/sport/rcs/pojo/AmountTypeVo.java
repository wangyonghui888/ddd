package com.panda.sport.rcs.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AmountTypeVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 容量百分比
     **/
    private BigDecimal volumePercentage;


    /**
     * 100 为特特殊用户，200一般用户
     **/
    private Integer special;
    /**
     * 藏单类型 0 、用户；1、标签；2、设备类型；3、商户；5动态藏单
     */
    private Integer category;

    /**
     * 动态藏单比例
     **/
    private BigDecimal dynamicVolumePercentage = BigDecimal.ZERO;

    /**
     * 商户藏单比例
     **/
    private BigDecimal merchantVolumePercentage = BigDecimal.ZERO;

    /**
     * 设备藏单比例
     **/
    private BigDecimal equipmentVolumePercentage = BigDecimal.ZERO;


}
