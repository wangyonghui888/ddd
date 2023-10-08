package com.panda.sport.rcs.vo;

import com.panda.sport.rcs.enums.TradeEnum;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.log.annotion.format.LogFormatAnnotion;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.vo
 * @Description : 手动/自动切换，开/关/封/锁请求入参
 * @Author : Paca
 * @Date : 2020-07-16 11:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@Accessors(chain = true)
@LogFormatAnnotion
public class MarketStatusUpdateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操盘级别，1-赛事级别，2-玩法级别，3-盘口级别，4-玩法集级别，5-批量玩法级别,6-投注项级别（冠军赛事）
     *
     * @see TradeLevelEnum
     */
    @LogFormatAnnotion(name = "操盘级别")
    private Integer tradeLevel;

    /**
     * 运动种类ID
     */
    @LogFormatAnnotion(name = "赛种")
    private Long sportId;

    /**
     * 赛事ID
     */
    @LogFormatAnnotion(name = "赛事ID")
    private Long matchId;

    /**
     * 玩法ID
     */
    @LogFormatAnnotion(name = "玩法ID")
    private Long categoryId;

    /**
     * 子玩法ID
     */
    @LogFormatAnnotion(name = "子玩法ID")
    private Long subPlayId;

    /**
     * 位置ID
     */
    private String placeNumId;

    /**
     * 盘口ID
     */
    private String marketId;

    /**
     * 盘口值
     */
    private String marketValue;

    /**
     * 盘口位置
     */
    @LogFormatAnnotion(name = "盘口位置")
    private Integer marketPlaceNum;

    /**
     * 玩法集ID
     */
    @LogFormatAnnotion(name = "玩法集ID")
    private Long categorySetId;

    /**
     * 玩法集编码
     */
    private String playSetCode;


    /**
     * 子玩法ID集合
     */
    private List<Long> subPlayIds;

    /**
     * 状态，0-开，1-封，2-关，11-锁
     *
     * @see TradeStatusEnum
     */
    @LogFormatAnnotion(name = "操盘状态")
    private Integer marketStatus;

    /**
     * 操盘类型，0-自动操盘，1-手动操盘，2-自动加强操盘
     *
     * @see TradeEnum
     */
    @LogFormatAnnotion(name = "操盘模式")
    private Integer tradeType;

    /**
     * 切换操盘模式是否封盘标志
     */
    private Integer isSeal;

    /**
     * 更新人ID
     */
    @LogFormatAnnotion(name = "操作账号")
    private Integer updateUserId;

    /**
     * 是否属于前十五分钟快照赛事
     * 1是 0否
     */
    @LogFormatAnnotion(name = "是否赛前")
    private Integer matchSnapshot;

    /**
     * 数据源关盘标志，0-否，1-是
     */
    private Integer sourceCloseFlag;

    /**
     * 收盘标志，0-否，1-是
     */
    private Integer endFlag;

    /**
     * 1-切滚球标识，2-前十五分钟，3-数据商挡板，4-A+模式（让球0|0.5封盘），5-M模式（让球0|0.5封盘），6-球头改变独赢封盘
     *
     */
    private Integer linkedType;

    private String remark;

    /**
     * 新增盘口标志，0-普通切换，1-新增盘口切换
     */
    private Integer newFlag;

    /**
     * 操作来源，1-操盘手
     */
    private Integer operateSource;
    /**
     * 是否推送赔率
     */
    private Integer isPushOdds;


    /**
     * 比分类型  1：是当前比分， 2：角球 3：红牌
     */
    private String scoreType;

    /**
     * 比分
     */
    private String score;

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
     * 投注项ID
     */
    private String oddsId;
    /**
     * 投注项类型
     */
    private String oddsType;

    /**
     * 盘口类型，1-赛前盘，0-滚球盘
     */
    private Integer matchType;
    /**
     * 是否MTS
     */
    private Boolean isMts;
    /**
     * 玩法集展示开关 0关 1开
     */
    private Integer clientShow;
    /**
     * 1 滚球 0早盘
     */
    private Integer liveOddBusiness;

    private String linkId;

    /**
     * 操作類別編碼
     */
    private Integer operatePageCode;

    /**
     * 玩法集名称
     */
    private String playSetName;


    /**
     * 标准赛事编码. 用于管理的赛事id
     */
    private String matchManageId;
}
