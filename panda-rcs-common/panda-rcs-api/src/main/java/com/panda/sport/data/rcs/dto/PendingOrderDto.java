package com.panda.sport.data.rcs.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 预约注单入参
 */
@Data
public class PendingOrderDto implements Serializable {
    /**
     * 预约订单编号(require=true)
     */
    private String orderNo;

    /**
     * 用户id(require=true)
     */
    private Long userId;

    /**
     * 商户id(require=true)
     */
    private Long merchantId;

    /**
     * 赛事id(require=true)
     */
    private Long matchId;

    /**
     * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘 (require=true)
     */
    private Integer matchType;

    /**
     * 赛事种类 (require=true)
     */
    private Long sportId;

    /**
     * 盘口id
     */
    private Long marketId;

    /**
     * 投注项id
     */
    private Long oddsId;
    /**
     * 投注项名称(新加)
     */
    private String playOptionsName;

    /**
     * 标准玩法id,如全场让球为4 (require=true)
     */
    private Long playId;

    /**
     * 预约盘口值(require=true)
     */
    private String marketValue;

    /**
     * 预约注单下注金额，指的是下注本金2位小数，投注时x100,以分(require=true)
     */
    private Long betAmount;

    /**
     * 预约盘口赔率(require=true)
     */
    private String orderOdds;

    /**
     * 预约订单状态(0.待处理  1.成功  2.失败   3.会议取消订单)(require=true)
     */
    private Integer orderStatus;

    /**
     * (投注项类型) Over/Under(require=true)
     */
    private String oddType;

    /**
     * 注单胜率
     */
    private Integer winPercent;

    /**
     * 赛事所属时间期号(require=true)
     */
    private String dateExpect;
    /**
     * 操盘方信息
     */
    private String riskTrader;

    /**
     * 对阵信息
     */
    private String matchInfo;

    /**
     * 赛事名称
     */
    private String matchName;

    /**
     * 下注时间
     */
    private Long betTime;
    /**
     * ip地址
     */
    private String ip;
    /**
     * ip用户区域
     */
    private String ipArea;
    /**
     * 失败或者取消原因
     */
    private String remark;

    /**
     * 联赛id
     */
    private Long tournamentId;
    /**
     * 玩法名称
     */
    private String playName;
    /**
     * 投注类型(投注时下注的玩法选项)，规则引擎用
     */
    private String playOptions;


    /**
     * 一级标签 用户等级
     */
    private int userTagLevel ;
    /**
     * 1:手机，2：PC
     */
    private Integer deviceType;

}
