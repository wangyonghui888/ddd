package com.panda.sport.rcs.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-02-2022/2/13 19:13
 */
@Data
public class ConfigCashOutTradeItemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long matchId;

    private Integer marketType;

    private Integer leve;

    private Long marketCategoryId;

    private Integer matchPreStatus;

    private Integer categoryPreStatus;

    private Long cashOutMargin;
}
