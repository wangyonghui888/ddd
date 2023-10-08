package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 盘口位置统计表
 * </p>
 *
 * @author lithan auto
 * @since 2020-10-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsMarketNumStatis implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 体育种类
     */
    private Long sportId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Integer marketCategoryId;

    /**
     * 盘口位置
     */
    private Integer placeNum;

    /**
     * 投注项标识
     */
    private String oddsType;

    /**
     * 投注笔数（投注项）
     */
    private BigDecimal betOrderNum;

    /**
     * 投注量
     */
    private BigDecimal betAmount;

    /**
     * 盈利值
     */
    private BigDecimal profitValue;

    /**
     * 联赛id
     */
    private Long standardTournamentId;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 1 早盘  2 滚球
     */
    private Integer matchType;

    /**
     * 最大派奖金额
     */
    private BigDecimal paidAmount;


}
