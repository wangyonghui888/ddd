package com.panda.sport.rcs.console.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * <p>
 * 盘口设置表
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-25
 */
@Data
@Table(name = "rcs_match_market_config_logs")
public class RcsMatchMarketConfigLogs {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 赛事id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer matchId;

    /**
     * 联赛id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tournamentId;

    /**
     * 玩法id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long playId;

    /**
     * 盘口id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private String marketId;

    /**
     * 更新赔率时的对应赔率值
     */
    private String updateOdds;

    /**
     * 主队盘口值
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal homeMarketValue;

    /**
     * 客队盘口值
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal awayMarketValue;

    /**
     * margin值
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal margin;

    /**
     * 主一级限额
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long homeLevelFirstMaxAmount;

    /**
     * 主一级赔率变化率
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal homeLevelFirstOddsRate;

    /**
     * 主二级限额
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long homeLevelSecondMaxAmount;

    /**
     * 主二级赔率变化率
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal homeLevelSecondOddsRate;

    /**
     * 最大单注限额/派奖
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long maxSingleBetAmount;

    /**
     * 最大赔率
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal maxOdds;

    /**
     * 最小赔率
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal minOdds;

    /**
     * 是否使用数据源0：手动；1：使用数据源
     */
    private Long dataSource;

    /**
     * 创建人
     */
    private String createUser;
    /**
     * 修改人
     */
    private String modifyUser;

    /**
     * 客一级限额
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long awayLevelFirstMaxAmount;

    /**
     * 客二级限额
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long awayLevelSecondMaxAmount;

    /**
     * 客一级赔率变化率
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal awayLevelFirstOddsRate;

    /**
     * 客二级赔率变化率
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal awayLevelSecondOddsRate;

    /**
     * 盘口状态  已经不用
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer marketStatus;
    /**
     * 平衡值
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long balance;

    /**
     * 操盘类型 EU 欧盘 MY 马来盘
     */
    private String marketType;

    /**
     * @Description 主队水差
     * @Param
     * @Author Sean
     * @Date 15:36 2020/1/11
     * @return
     **/
    private String homeAutoChangeRate;
    /**
     * @Description 客队水差
     * @Param
     * @Author Sean
     * @Date 15:36 2020/1/11
     * @return
     **/
    private String awayAutoChangeRate;
    /**
     * @Description 和局水差
     * @Param
     * @Author Sean
     * @Date 15:36 2020/1/11
     * @return
     **/
    private String tieAutoChangeRate;

    /**
     * 0 否 1 是 自动封盘
     */
    private String autoBetStop;

    /**
     * 变更级别  1赛事   2玩法阶段  3盘口
     */
    private Integer changeLevel;
    /**
     * 赔率  投注项id+赔率
     */
    private String oddsValue;


    public RcsMatchMarketConfigLogs() {
        homeMarketValue = new BigDecimal(0);
        awayMarketValue = new BigDecimal(0);
    }
}
