package com.panda.sport.rcs.pojo.odd;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CashoutMarketMessage implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * 标准盘口id
     * 非空
     */
    private Long id;

    /**
     * 非空
     * 标准玩法id   standard_sport_market_category.id
     */
    private Long marketCategoryId;


    private String thirdMatchId;


    private String thirdMarketId;

    /**
     * 数据商是否支持提前结算
     */
    private Integer cashOutStatus;

    /**
     * 业务使用状态
     */
    private Integer matchPreStatus;

    /**
     * 赛事级别提前结算开关
     */
    private Integer matchPreStatusRisk;
    /**
     * 玩法级别提前结算开关
     */
    private Integer categoryPreStatus;

    /**
     * 阶段
     */
    private Integer matchPeriod;

    /**
     * 盘口状态
     */
    private Integer status;

    /**
     * 盘口类型
     */
    private Integer marketType;

}
