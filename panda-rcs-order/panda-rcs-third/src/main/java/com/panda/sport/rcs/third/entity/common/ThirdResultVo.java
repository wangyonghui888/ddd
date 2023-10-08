package com.panda.sport.rcs.third.entity.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/3/22 15:04
 * @description 定义一个三方统一的投注结果
 */
@Data
public class ThirdResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 第三方订单id
     */
    private String thirdNo;

    /**
     * 订单状态 - 转换
     * 0接拒中  1接单成功  2拒单
     */
    private Integer thirdOrderStatus;
    /**
     * 延时
     */
    private Integer delay;
    /**
     * 理由
     */
    private String reasonMsg;

    /**
     * 三方错误码
     */
    private String errorCode;

    /**
     * 第三方返回的结果
     */
    private String thirdRes;

    private Long maxAmount;
    private Long minAmount;

    /**
     * 是否可以接单
     */
    private Boolean isBetAllowed;

}
