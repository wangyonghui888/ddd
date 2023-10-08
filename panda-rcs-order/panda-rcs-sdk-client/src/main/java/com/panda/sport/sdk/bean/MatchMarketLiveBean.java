package com.panda.sport.sdk.bean;

import lombok.Data;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mongo
 * @Description :  TODO
 * @Date: 2019-10-25 19:15
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchMarketLiveBean {

    private String id;

    /**
     * 标准联赛ID
     */
    private Long matchId;
    
    private String redCardScore;
    
    private String yellowRedCardScore;
    
    private String yellowCardScore;

    /**
     * 比赛开盘标识. 0: 未开盘; 1: 开盘; 2: 关盘; 3: 封盘; 开盘后用户可下注
     */
    private Integer operateMatchStatus;

    /**
     * 赛事是否开放赛前盘. 取值为 1  或  0.  1=开放; 0=不开放
     */
    private Integer preMatchBusiness;

    /**
     * 赛事状态.  比如:未开赛, 滚球, 取消, 延迟等.
     */
    private Integer matchStatus;

    /**
     * 操盘类型 0:自动操盘 1:手动操盘
     */
    private Integer tradeType;
    /**
     * 赛事收藏状态
     */
    private boolean matchCollectStatus;

    private Long eventTime;
    /**
     * 体育种类ID
     */
    private Long sportId;
    /**
     * 标准赛事编码. 用于管理的赛事id
     */
    private String matchManageId;
    /**
     * 标准赛事联赛ID
     */
    private Long standardTournamentId;
    /**
     * 操盘手
     */
    private String traderName;

    /**
     * 联赛-开售滚球数
     */
    private Long rollNum;
    /**
     * 比赛开始时间
     */
    private String matchStartTime;

    private String matchStartDate;
    /**
     * 联赛分级。1: 一级联赛；2:二级联赛；3：三级联赛；以此类推；0：未分级
     */
    private Integer tournamentLevel;

    /**
     * 是否为中立场。取值为 0  和1  。  1:是中立场，0:非中立场。操盘人员可手动处理
     */
    private Integer neutralGround;

    /**
     * 联赛名称及编码
     */
    private Long tournamentNameCode;
    
    /**
     * 操盘平台 MTS ，PA
     */
    private String riskManagerCode;

    /**
     * 当前比分信息
     */
    private String score;
    /**
     * 最近比分队伍
     */
    private String recentScoreTeam;
    /**
     * 最近角球比分队伍
     */
    private String recentCornerScoreTeam;
    /**
     * 是否支持滚球。取值为 1 或 0 。1=支持；0=不支持
     */
    private Integer liveOddBusiness;
    /**
     * 次要玩法数量
     */
    private Integer playNum;
    private Integer secondsMatchStart;
    
    private Integer period;

    /**
     * 盘口数
     */
    private Integer marketCount;

    /**
     * 角球是否展示  0:不展示 1:展示
     */
    private Integer cornerShow;

    /**
     * 角球比分
     */
    private String  cornerScore;

    /**
     * 罚牌是否展示  0:不展示 1:展示
     */
    private Integer cardShow;

    /**
     * 罚牌比分
     */
    private String  cardScore;
    /**
     * 进入滚球时是否初始化期望值
     */
    private boolean initBetAmount = false;

    /**
     * 删除  0:否 1:是
     */
    private Integer isDelete;
    
    /**
     * 用于排序字段，联赛名称首字母，主客队首字母
     */
    private String nameConcat;
}
