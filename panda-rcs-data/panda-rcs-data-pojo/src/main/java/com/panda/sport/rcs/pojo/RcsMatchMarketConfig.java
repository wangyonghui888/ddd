package com.panda.sport.rcs.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class RcsMatchMarketConfig extends RcsBaseEntity<RcsMatchMarketConfig> {

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
    private Long tournamentId;

    /**
     * 玩法id
     */
    private Long playId;

    /**
     *  子玩法id
     */
    @TableField(exist = false)
    private String subPlayId;

    /**
     * 盘口id
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long marketId;

    /**
     * 盘口id
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long marketOddsId;

    @TableField(exist = false)
    private Long homeMarketId;
    /**
     * 更新赔率时的对应赔率值
     */
    private String updateOdds;

    /**
     * 主队盘口值
     * 主队盘口值
     */
    private BigDecimal homeMarketValue;

    /**
     * 客队盘口值
     */
    private BigDecimal awayMarketValue;

    /**
     * margin值
     */
    private BigDecimal margin;

    /**
     * 主一级限额
     */
    private Long homeLevelFirstMaxAmount;

    /**
     * 主一级赔率变化率
     */
    private BigDecimal homeLevelFirstOddsRate;

    /**
     * 主二级限额
     */
    private Long homeLevelSecondMaxAmount;

    /**
     * 主二级赔率变化率
     */
    private BigDecimal homeLevelSecondOddsRate;

    /**
     * 最大单注限额/派奖
     */
    private Long maxSingleBetAmount;

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
     * 客一级限额
     */
    private Long awayLevelFirstMaxAmount;

    /**
     * 客二级限额
     */
    private Long awayLevelSecondMaxAmount;

    /**
     * 客一级赔率变化率
     */
    private BigDecimal awayLevelFirstOddsRate;

    /**
     * 客二级赔率变化率
     */
    private BigDecimal awayLevelSecondOddsRate;

    /**
     * 盘口状态  已经不用
     */
    @TableField(exist = false)
    private Integer marketStatus;


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
    private  String homeAutoChangeRate;
    /**
     * 位置水差
     */
    private BigDecimal awayAutoChangeRate;
    /**
     * @Description   和局水差
     * @Param
     * @Author  Sean
     * @Date  15:36 2020/1/11
     * @return
     **/
    private  String tieAutoChangeRate;

    /**
     * 0 否 1 是 自动封盘
     */
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
    private Integer relevanceType;
    /**
     * 欧赔赔率差值
     */
    @TableField(exist = false)
    private Integer diffOdds;

    /**
     * 赔率变化累计值
     */
    private BigDecimal oddsChange;
    /**
     * 主队投注额
     */
    private Long homeAmount;
    /**
     * 客队投注额
     */
    private Long awayAmount;
    /**
     * 和投注额
     */
    private Long tieAmount;
    /**
     * 盘口配置位置
     */
    private Integer marketIndex;
    
    /**
     * 暂停margain
     */
    private BigDecimal timeOutMargin;

    /**
     * 1.来自融合的自动清理标识  2.手动清理标识   3，task比分清理标识  4.赛前切滚球清识标识  5.数据源切换清理标识
     */
    @TableField(exist = false)
    private Integer clearType;

    /**
     * 投注项类型
     */
    @TableField(exist = false)
    private String oddsType;


    public RcsMatchMarketConfig(Long matchId, Long tournamentId, Long playId, Long marketId) {
        this.matchId = matchId;
        this.tournamentId = tournamentId;
        this.playId = playId;
        this.marketId = marketId;
    }

    public RcsMatchMarketConfig() {
    }
}
