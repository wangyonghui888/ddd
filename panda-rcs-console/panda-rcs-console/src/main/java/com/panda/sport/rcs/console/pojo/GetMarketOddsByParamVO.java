package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetMarketOddsByParamVO {
    /**
     * 表ID, 自增
     */
    private Long id;
    /**
     * 原本id
     */
    private Long oId;

    /**
     * 盘口位置id
     */
    private String placeNumId;

    /**
     * 投注项类型
     */
    private String oddsType;

    private Integer active;

    /**
     * 附加字段1
     */
    private String addition1;

    /**
     * 附加字段2
     */
    private String addition2;

    /**
     * 附加字段3
     */
    private String addition3;

    /**
     * 附加字段4
     */
    private String addition4;

    /**
     * 附加字段5
     */
    private String addition5;


    /**
     * 投注项名称中包含的表达式的值
     */
    private String nameExpressionValue;

    /**
     * 投注项赔率. 单位: 0.00001
     */
    private Integer oddsValue;

    /**
     * 前一次投注项赔率. 单位: 0.00001
     */
    private Integer previousOddsValue;

    /**
     * 投注项原始赔率(第三方未调整水位的赔率). 单位:  0.00001
     */
    private Integer originalOddsValue;

    /**
     * 投注给哪一方: T1主队, T2客队,
     */
    private String targetSide;

    /**
     * 用于排序, 大于1, 越小越靠前
     */
    private Integer orderOdds;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    /**
     * 该字段用于做风控时, 需要替换成风控服务商提供的投注项id.  如果数据源发生切换, 当前字段需要更新.
     */
    private String thirdOddsFieldSourceId;

    /**
     * 是否需要人工确认开奖. 1 需要;  0 不需要
     */
    private Integer managerConfirmPrize;


    private String remark;

    /**
     * 扩展参数：用于上游需通过融合向下游透传的参数，融合不做任何处理及存储
     */
    private String extraInfo;

    private BigDecimal margin;

    /**
     * 水差值
     */
    private Double marketDiffValue;
    /**
     * 数据型式
     */
    private Integer dataType;
}
