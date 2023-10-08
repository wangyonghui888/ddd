package com.panda.rcs.logService.strategy;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.panda.rcs.logService.vo.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Z9-jing
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogAllBean  implements Serializable {

    private Long id;

    private Long matchId;

    private Long matchInfoId;

    private String traderCode;

    private Long updateUserId;

    private Integer matchType;

    private String distanceSwitch;

    private String beforeString;

    private String old;

    private String newValue;

    private String pendingOrderRate;


    private String userPendingOrderCount;

    private String businesPendingOrderPayVal;

    private String userPendingOrderPayVal;

    private String playSetCode;

    private Integer sportId;

    private List<CategoryVo> categoryList;

    private Integer marketIndex;

    private Long templateId;
    private BigDecimal marketDiffValue;

    private List<Long> categoryIdList;

    private Integer tradeLevel;


    private String mtsConfigValue;

    private RcsMarketCategorySet marketCategorySet;


    private Long categorySetId;

    private String playSetName;


    /**
     *预约赔付累计限额
     */
    private Long pendingOrderPayVal;

    /**
     * 累计差值
     */
    private Long multiDiffVal;
    /**
     * 累计概率变化
     */
    private BigDecimal multiOddsRate;

    /**
     * 擴展字段
     */
    private String addition1;

    /**
     * 自動開盤時間 (單位: 秒)
     */
    private Integer autoOpenTime;

    /**
     * 擴展字段
     */
    private String addition2;

    private String home;

    private String away;

    /**
     * 一级累计盘口跳水金额
     **/
    private BigDecimal levelFirstMarketAmount;
    /**
     * 二级累计盘口跳水金额
     **/
    private BigDecimal levelSecondMarketAmount;


    /**
     * 累计上盘一级盘口变化值
     **/
    private BigDecimal homeLevelFirstMarketRate;
    /**
     * 累计上盘二级盘口变化值
     **/
    private BigDecimal homeLevelSecondMarketRate;
    /**
     * 累计下盘一级盘口变化值
     **/
    private BigDecimal awayLevelFirstMarketRate;
    /**
     * 累计下盘二级盘口变化值
     **/
    private BigDecimal awayLevelSecondMarketRate;

    private Long maxSingleBetAmount;

    private BigDecimal margin;

    /**
     * 是否开启跳赔，0-否，1-是
     */
    private Integer isOpenJumpOdds;
    /**
     * 是否倍数跳赔，0-否，1-是
     */
    private Integer isMultipleJumpOdds;

    private  String awayAutoChangeRate;

    /**
     * 联动模式：0(否),1(是)
     */
    private Integer linkageMode;

    private Integer mode;
    /**
     * 是否开启跳盘，0-否，1-是
     */
    private Integer isOpenJumpMarket;
    /**
     * 是否倍数跳盘，0-否，1-是
     */
    private Integer isMultipleJumpMarket;

    /**
     * 操盘类型 EU 欧盘 MY 马来盘
     */
    private String marketType;
    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件类型
     */
    private String eventType;

    private Integer marketStatus;

    private Long playId;

    private String oddsType;

    private Integer tradeType;

    private Long marketId;

    private Long categoryId;

    private Integer oddsValue;

    /**
     * 1.常规接距 2.提前结算接距
     */
    private Integer rejectType;

    /**
     * 操作人ID
     */
    private String userId;

    /**
     * 赔率源
     */
    private String dataSourceCode;
    /**
     * 水差是否关联，0-不关联，1-关联
     */
    private Integer relevanceType;

    /**
     * margain值
     */
    private String margain;


    /**
     * 投注项数据
     */
    private List<OddsValueVo> oddsValueList;

    /**
     * 结算/审核事件
     */
    private List<TournamentTemplateEventVo> templateEventList;

    /**
     * 玩法集
     */
    private List<TournamentTemplateCategorySetVo> categorySetList;

    private List<TournamentTemplatePlayMargainParam> playMargainList;

    /**
     * 0 投注额差值 1 投注额/赔付混合差值
     */
    private Integer balanceOption;

    /**
     * 目标咬度（即目标盈利率）
     */
    private BigDecimal targetProfitRate;
    /**
     * 是否热门，1:是 0:否
     */
    private Integer isPopular;

    /**
     * 所属层级
     */
    private Integer level;

    private Integer switchStatus;


    private List<Map<String, Object>> oddsList;
    /**
     * SR  BC  BG
     */
    private String dataSource;

    /**
     * T常规
     */
    private Integer normal;

    private BigDecimal homeMarketValue;

    private List<BigDecimal> marketValues;

    private BigDecimal marketHeadGapOpt;
    private BigDecimal oddsChange;

    /**
     * T延时
     */
    private Integer minWait;

    /**
     * 最大延时
     */
    private Integer maxWait;

    /**
     * 需要修改的数据
     */
    List<LogAllBean>  events;

    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 早盘模板名称
     */
    private String preLemplateName;
    /**
     * 滚球模板名称
     */
    private String liveLemplateName;

    /**
     * 审核时间
     */
    private Integer eventHandleTime;

    /**
     *结算时间
     */
    private Integer settleHandleTime;

    /**
     * 时间轴值
     */
    private Long timeVal;

    /**
     * 根据type设置（1：设值联赛等级  2：设值联赛id   3：设值赛事id）
     */
    private Long typeVal;

    /**
     * 盘口值
     */
    private String marketValue;

    /**
     * ao数据源配置
     */
    private String aoConfigValue;


    /**
     * 商户单场赔付限额
     */
    private Long businesMatchPayVal;

    /**
     * 用户单场赔付限额
     */
    private Long userMatchPayVal;

    /**
     * 警示值
     */
    private BigDecimal cautionValue;

    /**
     * 操作頁面代碼
     */
    private Integer operatePageCode;

    /**
     * 比分源1:SR  2:UOF
     */
    private Integer scoreSource;

    /**
     * 玩法赔率源
     */
    private List<PlaysOddsConfig> playOddsConfigs;

    @Data
    public static class PlaysOddsConfig {
        /**
         * 玩法赔率源
         */
        private String dataSource;
        /**
         * 配置的玩法
         */
        private List<Long> playIds;
    }

    /**
     * 玩法赔率源
     */
    private List<BaijiaConfig> baijiaConfigs;



    @Data
    public static class BaijiaConfig {
        /**
         * 参考网名称
         */
        private String name;
        /**
         * 权重值
         */
        private Long value;
        /**
         * 是否勾选（0.否 1.是）
         */
        private Integer status;
    }

    /*** 数据源编码 ***/
    private String dataSouceCode;

    /*** 赛事管理id  即赛事id（模糊查询） */
    private String matchManageId;

    /**
     * 球队
     */
    private List<MatchTeamInfo> teamList;


   // private LogAllBean beforeParams;

    private Map<String,Object> beforeParams;

    /**
     * 模板名稱
     */
    private String templateName;

    /**
     * 玩法集名稱
     */
    @SerializedName(value = "categorySetName", alternate = {"categorySetName", "marketCategoryIds"})
    private String categorySetName;

    /**
     * 复制的玩法集名称
     */
    private String copyCategorySetName;

    /**
     * 弃用标志，1-弃用，0-启用
     */
    private Integer disableFlag;

    /**
     * 最大盘口数
     */
    private Integer marketCount;

    /**
     * 支持串关，1:是 0:否
     */
    private Integer isSeries;
    /**
     * 副盘限额比列
     */
    private String viceMarketRatio;
    /**
     * 相邻盘口差值
     */
    private BigDecimal marketNearDiff;
    /**
     * 相邻盘口赔率差值
     */
    private BigDecimal marketNearOddsDiff;
    /**
     * 赔率（水差）变动幅度
     */
    private BigDecimal oddsAdjustRange;
    /**
     * 盘口调整幅度
     */
    private BigDecimal marketAdjustRange;
    /**
     * 有效分时margin
     */
    private Long validMarginId;

    /**
     * 足球自动关盘时间设置：6、上半场期间 41、加时赛上半场 7、下半场期间 42、加时赛下半场
     * 篮球自动关盘时间设置：13、第1节 14、第2节 15、第3节 16、第4节 40、加时
     */
    private Integer autoCloseMarket;

    private Integer autoOpenMarket;

    /**
     * 手动操盘相邻盘口差
     */
    private BigDecimal manualMarketNearDiff;

    /**
     * 是否出涨自动封盘（0.关 1.开）
     */
    private Integer ifWarnSuspended;

    /**
     * 预约投注开关 0:关 1:开
     */
    private Integer pendingOrderStatus;

    /**
     * 跳水最大值
     */
    private BigDecimal oddsMaxValue;
    /**
     * 跳盘最大值
     */
    private BigDecimal marketMaxValue;

    /**
     * 赔率接拒变动范围
     */
    private BigDecimal oddsChangeValue;

    /**
     * 盘口出涨预警
     */
    private Integer marketWarn;

    /**
     * 赔率变动接拒开关（0.关 1.开）
     */
    private Integer oddsChangeStatus;

    /**
     * 比赛进程时间
     */
    private Integer matchProgressTime;

    /**
     * 补时时间
     */
    private Integer injuryTime;

    /**
     * 手动操盘相邻盘口赔率差
     */
    private BigDecimal manualMarketNearOddsDiff;

    /**
     * 赛事提前结算开关 0:关 1:开
     */
    private Integer matchPreStatus;

    /**
     * 是否特殊抽水1:是 0:否
     */
    private Integer isSpecialPumping;


    /**
     * 特殊抽水赔率区间
     */
    private String specialOddsInterval;
    /**
     * 高赔特殊抽水赔率区间
     */
    private String specialOddsIntervalHigh;
    /**
     * 高赔特殊抽水赔率区间
     */
    private String specialOddsIntervalLow;
    /**
     * 特殊抽水赔率区间状态
     */
    private String specialOddsIntervalStatus;
    /**
     * 高赔特殊抽水保底投注区间
     */
    private String specialBettingIntervalHigh;

    /**
     * 最大赔率
     */
    private BigDecimal maxOdds;
    /**
     * 最小赔率
     */
    private BigDecimal minOdds;

    /**
     * 跳赔规则
     * 0 累计/单枪跳分 1 累计差值跳分
     */
    private Integer oddChangeRule;
    /**
     * 上盘累计限额
     */
    private Long homeMultiMaxAmount;
    /**
     * 上盘单枪限额
     */
    private Long homeSingleMaxAmount;
    /**
     * 上盘单枪赔率变化率
     */
    private BigDecimal homeSingleOddsRate;
    /**
     * 上盘累计赔率变化率
     */
    private BigDecimal homeMultiOddsRate;

    /**
     * 下盘累计赔率变化率
     */
    private BigDecimal awayMultiOddsRate;

    /**
     * 下盘单枪赔率变化率
     */
    private BigDecimal awaySingleOddsRate;

    /**
     * 上盘一级限额
     */
    private Long homeLevelFirstMaxAmount;
    /**
     * 上盘二级限额
     */
    private Long homeLevelSecondMaxAmount;
    /**
     * 上盘一级赔率变化率
     */
    private BigDecimal homeLevelFirstOddsRate;
    /**
     * 上盘二级赔率变化率
     */
    private BigDecimal homeLevelSecondOddsRate;
    /**
     * 下盘一级赔率变化率
     */
    private BigDecimal awayLevelFirstOddsRate;

    /**
     * 下盘二级赔率变化率
     */
    private BigDecimal awayLevelSecondOddsRate;

    /**
     * 单注投注/赔付限额 单注赔付限额
     */
    private Long orderSinglePayVal;

    /**
     * 用户累计赔付限额
     */
    private Long userMultiPayVal;
    /**
     * 玩法提前结算开关 0:关 1:开
     */
    private Integer categoryPreStatus;
    /**
     * CashOut Margin
     */
    private Long cashOutMargin;


    /**
     * 自动关盘比分设置 1勾选 0未勾选
     */
    private Integer isAutoCloseScoreConfig;

    /**
     * 当达到X分时关盘
     */
    private Integer achieveCloseScore;
    /**
     * 开关
     */
    private Integer clientShow;



}


