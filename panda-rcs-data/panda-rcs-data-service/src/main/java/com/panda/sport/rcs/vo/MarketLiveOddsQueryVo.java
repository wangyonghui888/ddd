package com.panda.sport.rcs.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 赛事盘口赔率查询件VO
 */

@Data
public class MarketLiveOddsQueryVo extends PageQuery{
    /**
     * 赔率类型:欧洲盘：OU，香港盘：HK
     */
    private String marketOddsKind;
    /**
     * 查询日期(前端传入的字符串，格式:yyyy-MM-dd)
     */
    private String matchDate;
    /**
     * 比赛开始时间
     */
    private Date beginTime;

    /**
     * 赛事收藏状态
     */
    private boolean matchCollectStatus;

    /**liveOddBu
     * 是否支持滚球:1=支持；0=不支持(变更为是否开滚球，对应siness字段)
     */
    private Integer liveOddBusiness;

    /**
     * 标准联赛 id. 对应联赛 id  对应  standard_sport_tournament.id
     */
    private Long standardTournamentId;

    private List<Long> tournamentIds;
    /**
     * 比赛ID
     */
    private Long matchId;
    /**
     * 联赛级别
     */
    private Integer tournamentLevel;
    /**
     * 比赛开盘标识: 0: 未开盘; 1: 开盘; 2: 关盘; 3: 封盘; 开盘后用户可下注
     *
     *   滚球-- 盘口状态 1 开放中 0 关闭中
     *
     */
    private Integer operateMatchStatus;

    /**
     * 盘口状态 1 开放中 0 关闭中
     */
    private Integer marketStatus;

    /**
     * 操盘类型 0:自动操盘 1:手动操盘 null 表示不修改当前操盘类型
     */
    private Integer tradeType;

    /**
     * 标准赛事编码. 用于管理的赛事id
     */
    private String matchManageId;

    /**
     * 体育类型ID
     */
    private Long sportId;

    /**
     * 比赛开始时间utc
     */
    private Long beginTimeMillis;

    /**
     * 比赛结束时间utc
     */
    private Long endTimeMillis;

    /**
     * 现在时间
     */
    private Long currentTimeMillis;

    /**
     * 更新时间
     */
    private Long updateTimeMillis;

    /**
     * 是否其它早盘 1：其它早盘
     */
    private Integer isOtherEarly;

    /**
     * 操盘手ID
     */
    private Long tradeId;

    private Long marketCategoryId;

    private Integer categoryPhase;
    /**
     * 入参玩法条件
     */
    private List<Long> marketCategoryIds;

}
