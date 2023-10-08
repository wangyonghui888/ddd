package com.panda.sport.data.rcs.dto.tournament;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 查询赛事模板相关字段 返回参数VO
 *
 * @description:
 * @author: Waldkir
 * @date: 2022-01-02 14:14
 */
@Data
public class MatchTemplateDataResVo implements java.io.Serializable{
    /**
     * 赛种
     */
    private Integer sportId;
    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 1：早盘；0：滚球
     */
    private Integer matchType;

    /**
     * mts接距配置信息
     */
    private String mtsConfigValue;

    /**
     * 接距开关（0.关 1.开）默认0
     */
    private Integer distanceSwitch;

    /**
     * 赔率源
     */
    private String dataSourceCode;

    /**
     * 商户单场赔付限额
     */
    private Long businesMatchPayVal;

    /**
     * 用户单场赔付限额
     */
    private Long userMatchPayVal;

    /**
     * 比分源1:SR  2:UOF
     */
    private Integer scoreSource;

    /**
     * 常规接单等待时间
     */
    private Integer normalWaitTime;
    /**
     * 暂停接单等待时间
     */
    private Integer pauseWaitTime;
    /**
     * 赛事提前结算开关 0:关 1:开
     */
    private Integer matchPreStatus;
    /**
     * 赔率变动接拒开关（0.关 1.开）
     */
    private Integer oddsChangeStatus;

    /**
     * 是否出涨自动封盘（0.关 1.开）
     */
    private Integer ifWarnSuspended;

    /**
     * 	警示值
     */
    private BigDecimal cautionValue;

    /**
     * 	百家赔各参考网值
     */
    private String baijiaConfigValue;
    /**
     * ao数据源配置信息
     */
    private String aoConfigValue;

    /**
     * 预约投注开关 0:关 1:开
     */
    private Integer pendingOrderStatus;
    /**
     * 商户单场预约赔付限额
     */
    private Long businesPendingOrderPayVal;
    /**
     * 用户单场预约赔付限额
     */
    private Long userPendingOrderPayVal;
    /**
     * 用户预约中笔数
     */
    private Integer userPendingOrderCount;
    /**
     * 预约投注速率
     */
    private Integer pendingOrderRate;
    /**
     * 提交结算开关数据源配置 {"SR":1,"AO":0}  1表示选中
     */
    private String earlySettStr;
}