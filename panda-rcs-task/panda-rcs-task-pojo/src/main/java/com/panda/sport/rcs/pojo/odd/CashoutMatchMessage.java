package com.panda.sport.rcs.pojo.odd;

import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.io.Serializable;
import java.util.List;

@Data
public class CashoutMatchMessage implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     *  赛种ID
     */
    private Long sportId;
    
    /**
     * 标准比赛ID   standard_match_info.id
     */
    private Long standardMatchInfoId;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    private Long modifyTime;
    /**
     * 业务使用状态
     */
    private Integer matchPreStatus;

    /**
     * 赛事级别提前结算开关
     */
    private Integer matchPreStatusRisk;

    /**
     * 是否显示罚球
     * 默认展示 * Y：展示；N：不展示
     */
    private String displayPenalty;
    /**
     * 多盘口玩法展示盘口个数
     */
    private Integer displayMarketCount;
    /**
     *  比赛开盘标识
     *  0:active 开盘, 1:suspended 封盘, 2:deactivated 关盘,11:锁盘状态
     */
    private Integer status;

    /**
     * 早盘滚球
     */
    private Integer matchType;
	/**
	 * 盘口投注项
	 */
	private List<CashoutMarketMessage> marketPreResultMessages;
}
