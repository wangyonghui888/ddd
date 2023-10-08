package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  联赛盘口参数设置表对应的实体
 * @Date: 2019-10-23 16:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@TableName("rcs_tournament_market_config")
@EqualsAndHashCode(callSuper = false)
public class RcsTournamentMarketConfig extends RcsBaseEntity<RcsTournamentMarketConfig> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 联赛id
     **/
    private Integer tournamentId;
    /**
     * 玩法id
     **/
    private Integer playId;

    /**
     * margin（抽水）
     **/
    private BigDecimal margin;
    /**
     * 主队一级限额
     **/
    private Long homeLevelFirstMaxAmount;
    /**
     * 主队一级赔率变化率
     **/
    private BigDecimal homeLevelFirstOddsRate;
    /**
     * 主队二级限额
     **/
    private Long homeLevelSecondMaxAmount;
    /**
     * 主队二级赔率变化率
     **/
    private BigDecimal homeLevelSecondOddsRate;

    /**
     * 客队一级赔率变化率
     **/
    private BigDecimal awayLevelFirstOddsRate;

    /**
     * 客队二级赔率变化率
     **/
    private BigDecimal awayLevelSecondOddsRate;
    /**
     * 最大单注限额
     **/
    private Long maxSingleBetAmount;
    /**
     * 最大赔率
     **/
    private BigDecimal maxOdds;
    /**
     * 最小赔率
     **/
    private BigDecimal minOdds;
    /**
     * 是否使用数据源0：自动；1：使用数据源  联赛不适用这个字段
     **/
    @TableField(exist = false)
    private Integer dataSource;

    /**
     * 创建时间
     */
    @TableField(exist = false)
    private Timestamp createTime;
    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 修改人
     */
    private String modifyUser;
    /**
     * 修改时间
     */
    @TableField(exist = false)
    private Timestamp modifyTime;
}
