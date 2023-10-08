package com.panda.sport.rcs.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 盘口位置统计表 ws推送用
 * </p>
 *
 * @author lithan auto
 * @since 2020-10-03
 */
@Data
public class RcsMarketNumStatisVo implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 用于排序, 大于1, 越小越靠前
     */
    private Integer orderOdds;


}
