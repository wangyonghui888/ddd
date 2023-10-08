package com.panda.sport.rcs.third.entity.common.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 标注投注项vo
 */
@Data
public class StandardMarketOddsVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 标准投注项ID
     */
    private Long id;
    /**
     * 投注项赔率. 单位: 0.00001
     */
    private Integer paOddsValue;
}