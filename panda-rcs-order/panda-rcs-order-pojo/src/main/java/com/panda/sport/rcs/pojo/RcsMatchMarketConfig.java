package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ObjectUtils;

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
@Accessors(chain = true)
public class RcsMatchMarketConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 盘口新id
     */
    @TableField(exist = false)
    private String newId;

    /**
     * 赛事id
     */
    private Long matchId;

    /**
     * 联赛id
     */
    @TableField(exist = false)
    private Long tournamentId;

    /**
     * 玩法id
     */
    private Long playId;
    
    @TableField(exist = false)
    private Integer sportId;

    /**
     * 盘口id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(exist = false)
    private Long marketId;

    @TableField(exist = false)
    private Long homeMarketId;
    /**
     * 更新赔率时的对应赔率值
     */
    @TableField(exist = false)
    private String updateOdds;

//    /**
//     * 接单等待时间
//     */
//    private Integer waitSeconds;
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
     * margin值
     */
    private BigDecimal margin;
    /**
     * 暂停margin值
     */
    private BigDecimal timeOutMargin;
    /**
     * 接单等待时间
     */
    @TableField(exist = false)
    private Integer waitSeconds;
    /**
     * 暂停接单等待时间
     */
    @TableField(exist = false)
    private Integer timeOutWaitSeconds;

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
     * 盘口状态  已经不用
     */
    @TableField(exist = false)
    private Integer marketStatus;

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
//    private  String homeAutoChangeRate;
    /**
     * @Description   客队水差
     * @Param
     * @Author  Sean
     * @Date  15:36 2020/1/11
     * @return
     **/
    private  String awayAutoChangeRate;
    /**
     * @Description   和局水差
     * @Param
     * @Author  Sean
     * @Date  15:36 2020/1/11
     * @return
     **/
//    private  String tieAutoChangeRate;

    /**
     * 0 否 1 是 自动封盘
     */
    @TableField(exist = false)
    private String autoBetStop;
    /**
     * 盘口值调整幅度
     */
    @TableField(exist = false)
    private BigDecimal marketAdjustRange;
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
    @TableField(exist = false)
    private Integer relevanceType;
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
     * 盘口配置位置
     */
    private Integer marketIndex;
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
     * 是否开启跳赔，0-否，1-是
     */
    private Integer isOpenJumpOdds;
    /**
     * 是否倍数跳赔，0-否，1-是
     */
    private Integer isMultipleJumpOdds;
    /**
     * 是否开启跳盘，0-否，1-是
     */
    private Integer isOpenJumpMarket;
    /**
     * 是否倍数跳盘，0-否，1-是
     */
    private Integer isMultipleJumpMarket;
    /**
     * 跳盘值
     **/
    @TableField(exist = false)
    private BigDecimal marketHeadGap;
    /**
     * 投注项
     **/
    @TableField(exist = false)
    private String oddsType;
    /**
     * 联动模式：0(否),1(是)
     */
    private Integer linkageMode;
    /**
     * 关盘消息
     */
    @TableField(exist = false)
    private String closeMsg;
    /**
     * 子玩法id
     */
    @TableField(exist = false)
    private String subPlayId;

    public RcsMatchMarketConfig(Long matchId, Long tournamentId, Long playId, Long marketId) {
        this.matchId = matchId;
        this.tournamentId = tournamentId;
        this.playId = playId;
        this.marketId = marketId;
    }

    public RcsMatchMarketConfig() {
    }

    public BigDecimal getHomeMargin(){
        return ObjectUtils.isEmpty(this.homeMargin) ? this.margin :this.homeMargin;
    }
    public BigDecimal getAwayMargin(){
        return ObjectUtils.isEmpty(this.awayMargin) ? this.margin :this.awayMargin;
    }
    public BigDecimal getTieMargin(){
        return ObjectUtils.isEmpty(this.tieMargin) ? this.margin :this.tieMargin;
    }

    /**
     * 累计跳盘限额/一级累计跳盘限额
     *
     * @return
     */
    public BigDecimal getJumpMarketOneLimit() {
        // 跳盘机制，0-累计/单枪跳盘，1-累计差值跳盘
        if (NumberUtils.INTEGER_ONE.equals(oddChangeRule)) {
            // 一级累计跳盘限额
            return levelFirstMarketAmount;
        } else {
            // 累计跳盘限额
            return marketCumulativeMaxAmount;
        }
    }

    /**
     * 单枪跳盘限额/二级累计跳盘限额
     *
     * @return
     */
    public BigDecimal getJumpMarketSecondLimit() {
        // 跳盘机制，0-累计/单枪跳盘，1-累计差值跳盘
        if (NumberUtils.INTEGER_ONE.equals(oddChangeRule)) {
            // 二级累计跳盘限额
            return levelSecondMarketAmount;
        } else {
            // 单枪跳盘限额
            return marketSingleMaxAmount;
        }
    }
}
