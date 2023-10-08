package com.panda.sport.rcs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TOrderHide implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单单号
     */
    private String orderNo;


    /**
     * 藏单类型 0 、用户；1、标签；2、设备类型；3、商户；
     * */
    private Integer category;

    /**
     * 货量百分比
     */
    private BigDecimal volumePercentage;
    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 动态藏单比例
     **/
    private BigDecimal dynamicVolumePercentage;

    /**
     * 商户藏单比例
     **/
    private BigDecimal merchantVolumePercentage;

    /**
     * 设备藏单比例
     **/
    private BigDecimal equipmentVolumePercentage;

}
