package com.panda.sport.rcs.pojo;


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
public class RcsMatchMarketConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 盘口新id
     */
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
     * 盘口id
     */
    private Long marketId;

    /**
     * 更新赔率时的对应赔率值
     */
    private String updateOdds;

    /**
     * 主队盘口值
     */
    private BigDecimal homeMarketValue = new BigDecimal("0");

    /**
     * 客队盘口值
     */
    private BigDecimal awayMarketValue = new BigDecimal("0");

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
    private Timestamp createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改时间
     */
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
    private Integer marketStatus;


    /**
     * 盘口是否改变
     */
    private Integer isMarketChange;

    /**
     * 盘口是否存在
     */
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
    private List<Map<String, Object>> oddsList;

    /**
     * 盘口状态 前端展示用
     */
    private Boolean marketActive;

    /**
     * 操盘类型 EU 欧盘 MY 马来盘
     */
    private String marketType;

    /**
     * 0 否 1 是 自动封盘
     */
    private String autoBetStop;

    public RcsMatchMarketConfig(Long matchId, Long tournamentId, Long playId, Long marketId) {
        this.matchId = matchId;
        this.tournamentId = tournamentId;
        this.playId = playId;
        this.marketId = marketId;
    }

    public RcsMatchMarketConfig() {
        homeMarketValue = new BigDecimal(0);
        awayMarketValue = new BigDecimal(0);
    }
}
