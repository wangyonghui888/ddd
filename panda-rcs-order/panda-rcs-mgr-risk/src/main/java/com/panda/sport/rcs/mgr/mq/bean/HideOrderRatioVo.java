package com.panda.sport.rcs.mgr.mq.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HideOrderRatioVo {

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 商户code
     */
    private String merchantCode;

    /**
     * 赛种id
     */
    private int sportId;

    private String sportNameZs;

    /**
     * 动态藏单比例
     */
    private BigDecimal dynamicHiddenRatio;
}
