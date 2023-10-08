package com.panda.sport.rcs.console.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentTemplateExcelVO extends BaseRowModel {

    /**
     * sportId
     */
    @ExcelProperty(index = 0)
    private Integer sportId;
    /**
     * 玩法id
     */
    @ExcelProperty(index = 1)
    private Integer playId;
    /**
     * 玩法名称
     */
    @ExcelProperty(index = 2)
    private String playName;
    /**
     * 等级
     */
    @ExcelProperty(index = 3)
    private Long level;
    /**
     * 1：早盘；0：滚球
     */
    @ExcelProperty(index = 4)
    private Integer matchType;
    /**
     * MY：马来盘  EU：欧洲盘  Other：其他
     */
    @ExcelProperty(index = 5)
    private String marketType;
    /**
     * 是否开售 1：是  0：否
     */
    @ExcelProperty(index = 6)
    private Integer isSell;
    /**
     * 最大盘口数
     */
    @ExcelProperty(index = 7)
    private Integer marketCount;
    /**
     * 副盘限额比列
     */
    @ExcelProperty(index = 8)
    private String viceMarketRatio;
    /**
     * 足球自动关盘时间设置：6、上半场期间 41、加时赛上半场 7、下半场期间 42、加时赛下半场
     * 篮球自动关盘时间设置：13、第1节 14、第2节 15、第3节 16、第4节 40、加时
     */
    @ExcelProperty(index = 9)
    private Integer autoCloseMarket;
    /**
     * 比赛进程时间
     */
    @ExcelProperty(index = 10)
    private Integer matchProgressTime;
    /**
     * 补时时间
     */
    @ExcelProperty(index = 11)
    private Integer injuryTime;
    /**
     * 盘口出涨预警
     */
    @ExcelProperty(index = 12)
    private Integer marketWarn;
    /**
     * 支持串关，1:是 0:否
     */
    @ExcelProperty(index = 13)
    private Integer isSeries;

    /**
     * 相邻盘口差值
     */
    @ExcelProperty(index = 14)
    private BigDecimal marketNearDiff;
    /**
     * 相邻盘口赔率差值
     */
    @ExcelProperty(index = 15)
    private BigDecimal marketNearOddsDiff;
    /**
     * 赔率（水差）变动幅度
     */
    @ExcelProperty(index = 16)
    private BigDecimal oddsAdjustRange;
    /**
     * 盘口调整幅度
     */
    @ExcelProperty(index = 17)
    private BigDecimal marketAdjustRange;
    /**
     * margain值
     */
    @ExcelProperty(index = 18)
    private String margain;
    /**
     * 单注投注/赔付限额
     */
    @ExcelProperty(index = 19)
    private Long orderSinglePayVal;
    /**
     * 用户累计赔付限额
     */
    @ExcelProperty(index = 20)
    private Long userMultiPayVal;
    /**
     * 0 投注额差值 1 投注额/赔付混合差值
     */
    @ExcelProperty(index = 21)
    private Integer balanceOption;
    /**
     * 最大赔率
     */
    @ExcelProperty(index = 22)
    private BigDecimal maxOdds;
    /**
     * 最小赔率
     */
    @ExcelProperty(index = 23)
    private BigDecimal minOdds;
    /**
     * 跳赔规则
     * 0 累计/单枪跳分 1 累计差值跳分
     */
    @ExcelProperty(index = 24)
    private Integer oddChangeRule;
    /**
     * 上盘累计限额
     */
    @ExcelProperty(index = 25)
    private Long homeMultiMaxAmount;
    /**
     * 上盘单枪限额
     */
    @ExcelProperty(index = 26)
    private Long homeSingleMaxAmount;
    /**
     * 上盘累计赔率变化率
     */
    @ExcelProperty(index = 27)
    private BigDecimal homeMultiOddsRate;
    /**
     * 上盘单枪赔率变化率
     */
    @ExcelProperty(index = 28)
    private BigDecimal homeSingleOddsRate;
    /**
     * 下盘累计赔率变化率
     */
    @ExcelProperty(index = 29)
    private BigDecimal awayMultiOddsRate;
    /**
     * 下盘单枪赔率变化率
     */
    @ExcelProperty(index = 30)
    private BigDecimal awaySingleOddsRate;
    /**
     * 累计差值
     */
    @ExcelProperty(index = 31)
    private Long multiDiffVal;
    /**
     * 累计概率变化
     */
    @ExcelProperty(index = 32)
    private BigDecimal multiOddsRate;
    /**
     * 上盘一级限额
     */
    @ExcelProperty(index = 33)
    private Long homeLevelFirstMaxAmount;
    /**
     * 上盘二级限额
     */
    @ExcelProperty(index = 34)
    private Long homeLevelSecondMaxAmount;
    /**
     * 上盘一级赔率变化率
     */
    @ExcelProperty(index = 35)
    private BigDecimal homeLevelFirstOddsRate;
    /**
     * 上盘二级赔率变化率
     */
    @ExcelProperty(index = 36)
    private BigDecimal homeLevelSecondOddsRate;
    /**
     * 下盘一级赔率变化率
     */
    @ExcelProperty(index = 37)
    private BigDecimal awayLevelFirstOddsRate;
    /**
     * 下盘二级赔率变化率
     */
    @ExcelProperty(index = 38)
    private BigDecimal awayLevelSecondOddsRate;
    /**
     * 暂停margin
     */
    @ExcelProperty(index = 39)
    private String pauseMargain;
    /**
     * 常规接单等待时间
     */
    @ExcelProperty(index = 40)
    private Integer normalWaitTime;
    /**
     * 暂停接单等待时间
     */
    @ExcelProperty(index = 41)
    private Integer pauseWaitTime;
    /**
     * 拒单盘口变动阈值
     */
    @ExcelProperty(index = 42)
    private BigDecimal rejectMarketDiff;
    /**
     * 拒单赔率变动阈值
     */
    @ExcelProperty(index = 43)
    private BigDecimal rejectOddsDiff;
    /**
     * 跳水最大值
     */
    @ExcelProperty(index = 44)
    private BigDecimal oddsMaxValue;
    /**
     * 跳盘最大值
     */
    @ExcelProperty(index = 45)
    private BigDecimal marketMaxValue;
    /**
     * 最小球头
     */
    @ExcelProperty(index = 46)
    private BigDecimal minBallHead;
    /**
     * 最大球头
     */
    @ExcelProperty(index = 47)
    private BigDecimal maxBallHead;
    /**
     * 赛制胜场
     */
    @ExcelProperty(index = 48)
    private Integer competitionWinValue;

}