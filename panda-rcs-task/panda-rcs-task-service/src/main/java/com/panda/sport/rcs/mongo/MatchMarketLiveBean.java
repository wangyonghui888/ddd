package com.panda.sport.rcs.mongo;

import com.panda.sport.rcs.vo.I18nItemVo;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

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
@Document(collection = "match_market_live")
public class MatchMarketLiveBean {


    @Id
    @Field(value = "id")
    private String id;

    /**
     * 比赛开始时间
     */
    private String matchStartTime;

    private String matchStartDate;
    /**
     * 比赛进行时间
     */
    private Integer secondsMatchStart;

    private Long eventTime;

    private String eventCode;
    /**
     * 比赛阶段
     */
    private Integer period;

    /**
     * 体育种类ID
     */
    private Long sportId;

    /**
     * 标准赛事联赛ID
     */
    private Long standardTournamentId;

    /**
     * 联赛分级。1: 一级联赛；2:二级联赛；3：三级联赛；以此类推；0：未分级
     */
    private Integer tournamentLevel;

    /**
     * 标准赛事编码. 用于管理的赛事id
     */
    private String matchManageId;
    /**
     * 标准联赛ID
     */
    private Long matchId;

    /**
     * 当前赛事的玩法集
     */
    private List<Long> matchSetIds;

    /**
     * 赛事状态.  比如:未开赛, 滚球, 取消, 延迟等.
     */
    private Integer matchStatus;

    /**
     * 赛前滚球状态
     */
    private Integer oddsLive;

    /**
     * 操盘类型 0:自动操盘 1:手动操盘 2:智能操盘
     */
    private List<Integer> tradeType;

    /**
     * 赛事收藏状态
     */
    private boolean matchCollectStatus;
    /**
     * 联赛收藏状态
     */
    private boolean tournamentCollectStatus;

    /**
     * 比赛开盘标识. 0: 未开盘; 1: 开盘; 2: 关盘; 3: 封盘; 开盘后用户可下注
     */
    private Integer operateMatchStatus;

    /**
     * 赛事是否开放赛前盘. 取值为 1  或  0.  1=开放; 0=不开放
     */
    private Integer preMatchBusiness;

    /**
     * 是否支持滚球。取值为 1 或 0 。1=支持；0=不支持
     */
    private Integer liveOddBusiness;

    /**
     * 是否为中立场。取值为 0  和1  。  1:是中立场，0:非中立场。操盘人员可手动处理
     */
    private Integer neutralGround;

    /**
     * 賽前操盘手
     */
    private String preTraderName;
    /**
     * 滚球操盘手
     */
    private String liveTraderName;

    /**
     * 滚球操盘手id
     */
    private String liveTraderId;


    /**
     * 赛前操盘手id
     */
    private String preTraderId;

    /**
     * 赛前开售时间
     */
    private Long preMatchTime;
    /**
     * 盘口数
     */
    private Integer marketCount;

    private Integer liveMarketCount;
    /**
     * 操盘平台 MTS ，PA
     */
    private String riskManagerCode;

    /**
     * 当前比分信息
     */
    private String score;
    /**
     * 红牌比分
     */
    private String redCardScore;
    /**
     * 红黄牌比分
     */
    private String yellowRedCardScore;
    /**
     * 黄牌比分
     */
    private String yellowCardScore;

    /**
     * 罚牌比分
     */
    private String cardScore;

    /**
     * 角球比分
     */
    private String cornerScore;
    /**
     * 加时赛比分
     */
    private String extraTimeScore;

    /**
     * 最近比分队伍
     */
    private String recentScoreTeam;
    /**
     * 最近角球比分队伍
     */
    private String recentCornerScoreTeam;

    /**
     * 玩法总数量
     */
    private Integer categoryCount;
    /**
     * 自动操盘玩法数量
     */
    private Integer autoCount;
    /**
     * 手动操盘玩法数量
     */
    private Integer manualCount;

    /**
     * 自动+ 操盘玩法数量
     */
    private Integer autoAddCount;
    /**
     * 用于排序字段，联赛名称首字母，主客队首字母
     */
    private String nameConcat;

    /**
     * 联赛名称及编码
     */
    private Long tournamentNameCode;
    /**
     * 是否属于前十五分钟快照赛事
     * 1是 0否
     */
    private Integer matchSnapshot;

    /**
     * 赛事类型（默认1）{
     *     1：普通赛事
     *     2：电竞赛事
     *     3：篮球3x3(如果运动类型为篮球）
     * }
     */
    private Integer matchType;

    /**
     * 网球赛制
     */
    private Integer roundType;
    /**
     * 局数
     */
    private Integer setNum;

    /**
     * 发球方
     */
    private String servesFirst;
    /**
     * 是否错误完赛事件（普通足球阶段为999才会使用该字段，0:否，1:是）
     */
    private Integer isErrorEndEvent;
    /**
     * 排序
     */
    private Integer sort;

    /**
     * 完赛时间
     */
    private Long  endTime;

    /**\
     * 玩法集下对应信息
     */
    private List<MatchCatgorySetVo> setInfos;

    private List<I18nItemVo> tournamentNames;

    /**
     * 球队
     */
    private List<MatchTeamVo> teamList;
    /**
     * 网球比分
     */
    private List<ScoreVo> scoreVos;
    /**
     * 棒球比分
     */
    private List<BaseBallScoreVo> baseBallScoreVos;
    /**
     * 赛事级别结算状态
     */
    private String matchPreStatusRisk;

    /**
     * 第三方赛事信息
     */
    private String thirdMatchListStr;
}
