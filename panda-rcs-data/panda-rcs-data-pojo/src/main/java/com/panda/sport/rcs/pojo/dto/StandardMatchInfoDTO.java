package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description: 赛事查询结果单元对象
 * @date 2019/9/3 17:05
 * @ModificationHistory Who    When    What
 */
@Data
public class StandardMatchInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id。id
     */
    private Long id;

    /**
     * 体育种类id。运动种类id 对应sport.id
     */
    private Long sportId;

    /**
     * 标准联赛 id。对应联赛 id  对应  standard_sport_tournament.id
     */
    private Long standardTournamentId;

    /** 联赛别名称编码.联赛名称编码.用于多语言*/
    private Long leagueAsNameCode;

    /** 联赛别名多语言*/
    private List<I18nItemDTO> il8nLeagueAsNameList;

    /**
     * 比赛开盘标识。0：未开盘；1：开盘；2：关盘；3：封盘；开盘后用户可下注
     */
    private Integer operateMatchStatus;

    /**
     * 比赛开始时间。比赛开始时间 UTC时间
     */
    private Long beginTime;

    /**
     * 是否支持串关。1 支持串关;0 不支持串关
     */
    private Integer canParlay;

    /**
     * 是否为中立场。取值为 0  和1  。  1:是中立场，0:非中立场。操盘人员可手动处理
     */
    private Integer neutralGround;

    /**
     * 对用户可见。1：可见； 0：不可见
     */
    private Integer visible;

    /**
     * 标准赛事编码。用于管理的赛事id
     */
    private String matchManageId;

    /**
     * 风控服务商编码。sr bc pa 等。详见 数据源表 data_source中的code字段
     */
    private String riskManagerCode;

    /**
     * 数据来源编码。指的是当前赛事使用哪个数据供应商的数据。使用该数据，则使用该风控
     */
    private String dataSourceCode;

    /** 商业事件源编码如：SR,BC,BG*/
    private String businessEvent;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 第三方赛事原始id. 该厂比赛在第三方数据供应商中的id. 比如:  SportRadar 发送数据时, 这场比赛的ID.
     */
    private String thirdMatchSourceId;

    /**
     * 比赛名称编码。比赛名称编码. 用于多语言
     */
    private List<I18nItemDTO> il8nMatchPositionList;

    /**
     * 赛事对应的2个球队list
     */
    private List<StandardSportTeamDTO> sportTeamList;

    /**
     * 修改时间。
     */
    private Long modifyTime;

    /**
     * 赛事是否开放赛前盘. 取值为 1  或  0.  1=开放; 0=不开放
     */
    private Integer preMatchBusiness;

    /**
     * 赛事是否开放滚球. 取值为 1  或  0.  1=开放; 0=不开放
     */
    private Integer liveOddBusiness;

    /**
     * 比分.  仅显示 90分钟内的比分.
     */
    private String score;

    /**
     * 比赛阶段id. 取自基础表 : match_status.id
     */
    private Long matchPeriodId;
    /**
     * 彩票号.(爬虫爬取)
     */
    private String lotteryNumber;
    /**
     * 比赛是否结束. 0: 未结束;  1: 结束. （比赛彻底结束, 双方不再有加时赛, 点球大战, 且裁判宣布结束）
     */
    private Integer matchOver;

    /** 是否高热度赛事：0否1是*/
    private Integer hotMatchStatus;

    /**
     * 比赛进行时间. 单位:秒.例如:3分钟11秒,则该值是 191
     */
    private Integer secondsMatchStart;
    /**
     * 赛事状态.  比如:未开赛, 滚球, 取消, 延迟等.  取system_item_dic中的value字段
     */
    private Integer matchStatus;
    /**
     * 局数(赛制).数字,例如:5,7,代表最多打5局7局
     */
    private Integer roundType;
    /**
     * 联赛分级。1: 一级联赛；2:二级联赛；3：三级联赛；以此类推；0：未分级
     */
    private Integer tournamentLevel;

    /**
     * 赛事类型（默认1）{
     *     1：普通赛事
     *     2：电竞赛事
     *     3：篮球3x3(如果运动类型为篮球）
     *     4：MMA(如果运动类型为拳击）
     * }
     */
    private Integer matchType;

    /**
     * 数据供应商编码. 取值见: data_source.code
     */
    private String matchDataProviderCode;
    /**
     * 赛前开售时间
     */
    private Long preMatchTime;
    /**
     * 滚球开售时间
     */
    private Long liveOddTime;
    /**
     * 比赛场地的国际化编码
     */
    private Long matchPositionNameCode;
    /**
     * 赛前操盘平台 如： SR
     */
    private String preRiskManagerCode;
    /**
     * 赛前数据服务商 如： SR
     */
    private String preMatchDataProviderCode;
    /**
     * 滚球数据服务商 如： SR
     */
    private String liveMatchDataProviderCode;

    /**
     * 滚球操盘平台 如： SR、MTS
     */
    private String liveRiskManagerCode;

    /** 赛前操盘手id*/
    private String preTraderId;
    /** 赛前操盘手名字*/
    private String preTrader;
    /** 滚球操盘手id*/
    private String liveTraderId;
    /** 滚球操盘手名字*/
    private String liveTrader;

    /**
     * 未售Unsold，逾期未售Overdue_Unsold，申请延期  Apply_Delay，开售 Sold，取消预售  Cancel_Sold,申请取消   Apply_Cancel_Sold，停售 Stop_Sold
     */
    private String preMatchSellStatus;

    /**
     * 未售Unsold，逾期未售Overdue_Unsold，申请延期  Apply_Delay，开售 Sold，取消预售  Cancel_Sold,申请取消   Apply_Cancel_Sold，停售 Stop_Sold
     */
    private String liveMatchSellStatus;

    /** 赛事标签（ 0:普通 ,1:热门 ,2:推荐）*/
    private Integer label;

    /**
     * 比赛时长：足球全场时长，篮球每节时长
     */
    private Integer matchLength;
    /**
     * 赛季id
     */
    private String seasonId;

    /**
     * 早盘盘口数
     */
    private Integer displayMarketCount;

    /**
     * 滚球盘口数
     */
    private Integer liveMarketCount;


    /*** 三方赛事集合***/
    private List<ThirdMatchInfoDTO> thirdMatchInfoList;

    /**
     * 数据权限人id
     **/
    private String auditorId;

    /**
     * 赛事审核人账号
     **/
    private String auditor;

    /**
     * 赛前滚球状态，1（接收到滚球赔率）0（未接收到滚球赔率）
     */
    private Integer oddsLive;


}
