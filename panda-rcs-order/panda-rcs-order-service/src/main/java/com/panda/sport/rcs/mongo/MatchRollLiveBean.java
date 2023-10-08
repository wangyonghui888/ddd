package com.panda.sport.rcs.mongo;

import com.panda.sport.rcs.vo.I18nItemVo;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
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
@Document(collection = "match_roll_live")
public class MatchRollLiveBean {

    @Id
    @Field(value = "id")
    private String id;
    /**
     * 标准联赛ID
     */
    private Long matchId;

    /**
     * 标准赛事编码. 用于管理的赛事id
     */
    private String matchManageId;
    /**
     * 赛事收藏状态
     */
    private boolean matchCollectStatus;

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
     * 联赛名称及编码
     */
    private Long tournamentNameCode;
    /**
     * 联赛名称多语言
     */
    private List<I18nItemVo> tournamentNames;
    /**
     * 联赛-开售滚球数
     */
    private Integer rollNum;
    /**
     * 比赛开盘标识. 0: 未开盘; 1: 开盘; 2: 关盘; 3: 封盘; 开盘后用户可下注
     */
    private Integer operateMatchStatus;

    /**
     * 赛事是否开放赛前盘. 取值为 1  或  0.  1=开放; 0=不开放
     */
    private Integer preMatchBusiness;

    /**
     * 赛事状态  0:未开赛, 1:滚球, 2:暂停，3:结束 ，4:关闭，5:取消，6:放弃，7:延迟，8:未知，9:延期，10:中断
     */
    private Integer matchStatus;

    /**
     * 当前比分信息
     */
    private String score;
    /**
     * 封盘-0/开盘-1 作用于整个玩法阶段下的所有盘口
     */
    private Integer playPhaseStatus;
    /**
     * 玩法阶段 字典ballPhase
     */
    private Integer playPhaseType;
    /**
     * 操盘类型 0:自动操盘 1:手动操盘
     */
    private Integer tradeType;

    /**
     * 操盘手
     */
    private String traderName;

    /**
     * 比赛开始时间
     */
    private String matchStartTime;
    /**
     * 比赛时间--年月日
     */
    private String matchStartDate;

    /**
     * 是否为中立场。取值为 0  和1  。  1:是中立场，0:非中立场。操盘人员可手动处理
     */
    private Integer neutralGround;

    /**
     * 是否支持滚球。取值为 1 或 0 。1=支持；0=不支持
     */
    private Integer liveOddBusiness;
    /**
     * 玩法数量
     */
    private Integer playNum;

    /**
     * 球队
     */
    private List<MatchMarketLiveOddsVo.MatchMarketTeamVo> teamList;

    /**
     * 玩法
     */
    private List<MarketCategory> marketCategoryList;
    
    private Integer secondsMatchStart;
    
    private Integer period;

}
