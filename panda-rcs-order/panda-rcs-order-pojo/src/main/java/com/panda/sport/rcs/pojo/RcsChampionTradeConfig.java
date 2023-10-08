package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.log.annotion.format.LogFormatAnnotion;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Kir
 * @since 2021-06-08
 */
@Data
public class RcsChampionTradeConfig extends RcsBaseEntity<RcsChampionTradeConfig> {
    /**
     * 主键
     */
    private Long id;

    /**
     * 类型（1.商户玩法限额 2.用户玩法限额 3.用户单注限额 4.用户单项限额）
     */
    private Integer type;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法id
     */
    private Long playId;

    /**
     * 盘口id
     */
    private Long marketId;

    /**
     * 额度
     */
    private BigDecimal amount;

    /**
     * 投注项id（类型为4时有值）
     */
    private String oddsFieldsId;
}
