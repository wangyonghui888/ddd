package com.panda.sport.rcs.console.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  赛事
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-07
 */
@Data
public class StandardMatchInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 赛事id. id
     */
    private String matchId;

    /**
     * 体育种类id. 运动种类id 对应sport.id
     */
    private String sportId;

    /**
     * 标准联赛 id. 对应联赛 id  对应  standard_sport_tournament.id
     */
    private String standardTournamentId;

    /**
     * 第三方比赛id. 第三方比赛在 表 third_match_info 中的id
     */
    private String thirdMatchId;

    /**
     * 开赛后的时间. 单位:秒.例如:3分钟11秒,则该值是 191
     */
    private String secondsMatchStart;
    /**
     * 赛事是否开放赛前盘. 取值为 1  或  0.  1=开放; 0=不开放
     */
    private String preMatchBusiness;

    /**
     * 赛事是否开放滚球. 取值为 1  或  0.  1=开放; 0=不开放
     */
    private String liveOddBusiness;

    /**
     * 比赛开盘标识. 0: 未开盘; 1: 开盘; 2: 关盘; 3: 封盘; 开盘后用户可下注
     */
    private String operateMatchStatus;

    /**
     * 比赛开始时间. 比赛开始时间 UTC时间
     */
    private String beginTime;
    /**
     * 赛事状态.  比如:未开赛, 滚球, 取消, 延迟等. 
     */
    private String matchStatus;
    /**
     * 标准赛事编码. 用于管理的赛事id
     */
    private String matchManageId;
    /**
     * 数据来源编码. 取值见: data_source.code
     */
    private String dataSourceCode;

    /**
     * 第三方赛事原始id. 该厂比赛在第三方数据供应商中的id. 比如:  SportRadar 发送数据时, 这场比赛的ID. 
     */
    private String thirdMatchSourceId;

    /**
     * 赛事双方的对阵信息.格式:主场队名称 VS 客场队名称
     */
    private String homeAwayInfo;

    /**
     * 比赛阶段id. 取自基础表 : match_status.id
     */
    private String matchPeriodId;

    /**
     * 修改时间. 
     */
    private String updateTime;

    /*************************开售信息开始************************/
    /**
     * 赛前操盘平台.
     */
    private String preRiskManagerCode;
    /**
     * 赛前数据服务商.
     */
    private String preMatchDataProviderCode;

    /**
     * 滚球操盘平台.
     */
    private String liveRiskManagerCode;
    /**
     * 滚球数据服务商.
     */
    private String liveMatchDataProviderCode;
    /**
     * 开售信息是否支持滚球.
     */
    private String sellLiveOddBusiness;
    /**
     * 赛前开售时间.
     */
    private String preMatchTime;
    /**
     * 滚球开售时间.
     */
    private String liveOddTime;
    /**
     * 修商业事件源编码.
     */
    private String businessEvent;
    /**
     * 赛前开售状态.
     */
    private String preMatchSellStatus;
    /**
     * 滚球开售状态.
     */
    private String liveMatchSellStatus;
    /**
     * 视频源.
     */
    private String videoSource;
    /**
     * 联赛中文名.
     */
    private String tournamentNameCn;
    /**
     * 联赛英文名.
     */
    private String tournamentNameEn;
    /**
     * 开售修改时间.
     */
    private String sellUpdateTime;

    /***************************开售信息结束************************************/

    /***************************盘口信息开始***********************************/
    /**
     * 盘口id：.
     */
    private String marketId;
    /**
     * 标准比赛id.
     */
    private String standardMatchInfoId;
    /**
     * 标准玩法id.
     */
    private String playId;
    /**
     * 盘口类型.
     */
    private String marketType;
    /**
     * 操盘方式.
     */
    private String operationType;
    /**
     * 排序类型.
     */
    private String orderType;
    /**
     * 盘口级别.
     */
    private String oddsMetric;
    /**
     * 附加字段1.
     */
    private String addition1;
    /**
     * 附加字段2.
     */
    private String addition2;
    /**
     * 附加字段3.
     */
    private String addition3;
    /**
     * 附加字段4.
     */
    private String addition4;
    /**
     * 数据源.
     */
    private String marketDataSourceCode;
    /**
     * 盘口值.
     */
    private String marketValue;

    /***************************盘口信息结束***********************************/
}
