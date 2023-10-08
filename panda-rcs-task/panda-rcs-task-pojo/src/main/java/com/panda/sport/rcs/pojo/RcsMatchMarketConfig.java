package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 盘口设置表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RcsMatchMarketConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 赛事id
     */
    private Long matchId;
    /**
     * 玩法id
     */
    private Long playId;
    /**
     * 盘口位置
     */
    private Integer marketIndex;
    /**
     * margin值
     */
    private BigDecimal margin;
    /**
     * 暂停margin
     */
    private BigDecimal timeOutMargin;

    /**
     * 盘口新id
     */
    @TableField(exist = false)
    private String newId;

    /**
     * 联赛id
     */
    @TableField(exist = false)
    private Long tournamentId;

    /**
     * 盘口id
     */
    //@JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long marketId;

    @TableField(exist = false)
    private Long homeMarketId;
    /**
     * 更新赔率时的对应赔率值
     */
    @TableField(exist = false)
    private String updateOdds;

    /**
     * 主队盘口值
     * 主队盘口值
     */
    @TableField(exist = false)
    private BigDecimal homeMarketValue;

    /**
     * 客队盘口值
     */
    @TableField(exist = false)
    private BigDecimal awayMarketValue;

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
     * 上盘单枪限额
     */
    private Long homeSingleMaxAmount;
    /**
     * 上盘累计限额
     */
    private Long homeMultiMaxAmount;

    /**
     * 上盘单枪赔率变化率
     */
    private BigDecimal homeSingleOddsRate;

    /**
     * 上盘累计赔率变化率
     */
    private BigDecimal homeMultiOddsRate;

    /**
     * 下盘单枪赔率变化率
     */
    private BigDecimal awaySingleOddsRate;

    /**
     * 下盘累计赔率变化率
     */
    private BigDecimal awayMultiOddsRate;

    /**
     * 最大单注限额/派奖
     */
    private Long maxSingleBetAmount;

    /**
     * 用户累计赔付限额
     */
    @TableField(exist = false)
    private Long userMultiPayVal;

    /**
     * 最大可投金额，根据联赛配置获取
     */
    @TableField(exist = false)
    private BigDecimal maxBetAmount;

    /**
     * 最大赔率
     */
    private BigDecimal maxOdds;

    /**
     * 最小赔率
     */
    private BigDecimal minOdds;

    /**
     * 是否使用数据源0：手动；1：使用数据源
     */
    @TableField(exist = false)
    private Long dataSource;

    /**
     * 创建时间
     */
    @TableField(exist = false)
    private Timestamp createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改时间
     */
    @TableField(exist = false)
    private Timestamp modifyTime;

    /**
     * 修改人
     */
    private String modifyUser;



    /**
     * 状态，0-开，1-封，2-关，11-锁
     *
     * @see com.panda.sport.rcs.enums.MarketStatusEnum
     */
    @TableField(exist = false)
    private Integer marketStatus;

    @TableField(exist = false)
    private Integer thirdMarketSourceStatus;

    @TableField(exist = false)
    private Integer operateMatchStatus;

    /**
     * 平衡值计算规则
     * 0 投注额 1 投注额/赔付值组合
     */
    private Integer balanceOption;
    /**
     * 跳赔规则
     * 0 累计/单枪 1 差额累计
     */
    private Integer oddChangeRule;
    /**
     * 盘口是否改变
     */
    @TableField(exist = false)
    private Integer isMarketChange;

    /**
     * 盘口是否存在
     */
    @TableField(exist = false)
    private Integer isExistsMarket;
    /**
     * 平衡值
     */
    @TableField(exist = false)
    private Long balance;
    /**
     * @Description //赔率数据
     * @Param
     * @Author kimi
     * @Date 2019/12/11
     * @return
     **/
    @TableField(exist = false)
    private List<Map<String, Object>> oddsList;

    /**
     * 盘口状态 前端展示用
     */
    @TableField(exist = false)
    private Boolean marketActive;

    /**
     * 操盘类型 EU 欧盘 MY 马来盘
     */
    private String marketType;

    /**
     * @Description   主队水差
     * @Param
     * @Author  Sean
     * @Date  15:36 2020/1/11
     * @return
     **/
    @TableField(exist = false)
    private  String homeAutoChangeRate;
    /**
     * @Description   客队水差
     * @Param
     * @Author  Sean
     * @Date  15:36 2020/1/11
     * @return
     **/
    @TableField(exist = false)
    private  String awayAutoChangeRate;
    /**
     * @Description   和局水差
     * @Param
     * @Author  Sean
     * @Date  15:36 2020/1/11
     * @return
     **/
    @TableField(exist = false)
    private  String tieAutoChangeRate;

    /**
     * 0 否 1 是 自动封盘
     */
    @TableField(exist = false)
    private String autoBetStop;
    /**
     * 盘口关联 1 是
     */

    /**
     * @Description //盘口是否关联
     * @Param 1是关联
     * @Author kimi
     * @Date 2020/2/20
     * @return
     **/
//    private Integer relevanceType;
    /**
     * 欧赔赔率差值
     */
    @TableField(exist = false)
    private Integer diffOdds;

    /**
     * 赔率变化累计值
     */
    @TableField(exist = false)
    private BigDecimal oddsChange;
    /**
     * 主队投注额
     */
    @TableField(exist = false)
    private Long homeAmount;
    /**
     * 客队投注额
     */
    @TableField(exist = false)
    private Long awayAmount;
    /**
     * 和投注额
     */
    @TableField(exist = false)
    private Long tieAmount;
    /**
     * 主胜margin
     */
    @TableField(exist = false)
    private BigDecimal homeMargin;
    /**
     * 客胜margin
     */
    @TableField(exist = false)
    private BigDecimal awayMargin;
    /**
     * 平局margin
     */
    @TableField(exist = false)
    private BigDecimal tieMargin;
    /**
     * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
     */
    @TableField(exist = false)
    private Integer matchType;
    /**
     * 投注项类型
     */
    @TableField(exist = false)
    private String oddsType;
    /**
     * 1-没有联赛信息需要再次同步，0-完整数据不需处理
     */
    @TableField(exist = false)
    private Integer active;
    /**
     *
     * 常规等待时间
     */
    private Integer waitSeconds;

    private Integer timeOutWaitSeconds;

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
    /**
     * 单枪盘口跳水金额
     **/
    private BigDecimal marketSingleMaxAmount;
    /**
     * 累计盘口跳水金额
     **/
    private BigDecimal marketCumulativeMaxAmount;
    /**
     * 上盘单枪盘口变化值
     **/
    private BigDecimal homeSingleMarketRate;
    /**
     * 上盘累计盘口变化值
     **/
    private BigDecimal homeCumulativeMarketRate;
    /**
     * 下盘单枪盘口变化值
     **/
    private BigDecimal awaySingleMarketRate;
    /**
     * 下盘累计盘口变化值
     **/
    private BigDecimal awayCumulativeMarketRate;
    /**
     * 联动模式：0(否),1(是)
     */
    private Integer linkageMode;

    /**
     * 1.来自融合的自动清理标识  2.手动清理标识   3，task比分清理标识  4.赛前切滚球清识标识  5.数据源切换清理标识
     */
    @TableField(exist = false)
    private Integer clearType;

    /**
     * 附加字段，用户区分位置
     */
    @TableField(exist = false)
    private String subPlayId;
    /**
     * 塞种
     */
    @TableField(exist = false)
    private Long sportId;
    /**
     * 玩法提前结算开关 0:关 1:开
     */
    @TableField(exist = false)
    private Integer CategoryPreStatus;
    /**
     * CashOut Margin
     */
    @TableField(exist = false)
    private Long cashOutMargin;
    /**
     * 自动关盘比分设置 1勾选 0未勾选
     */
    private Integer isAutoCloseScoreConfig;

    /**
     * 当达到X分时关盘
     */
    private Integer achieveCloseScore;

    public RcsMatchMarketConfig(Long matchId, Long tournamentId, Long playId, Long marketId) {
        this.matchId = matchId;
        this.tournamentId = tournamentId;
        this.playId = playId;
        this.marketId = marketId;
    }

    public RcsMatchMarketConfig() {
    }
}