package com.panda.sport.rcs.pojo;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 篮球矩阵表
 * </p>
 *
 * @author lithan
 * @since 2021-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsPredictBasketballMatrix implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 运动种类
     */
    private Integer sportId;

    /**
     * 标准赛事id
     */
    private Long matchId;

    /**
     * 赛事类型:1赛前,2滚球
     */
    private Integer matchType;

    /**
     * 玩法id
     */
    private Integer playId;

    /**
     * 分数(总分/分差)
     */
    private Integer forecastScore;

    /**
     * 中值(总分/分差)
     */
    private Integer middleValue;

    /**
     * 预测盈利(庄家视角)
     */
    private BigDecimal profitAmount;

    /**
     * 创建时间
     */
    private Long createTime;


}
