package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 预测forecast表
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rcs_predict_forecast")
public class RcsPredictForecast implements Serializable {

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
     * 盘口id
     */
    private Long marketId;

    /**
     * 投注项
     */
    private String oddsItem;

    /**
     * 基准分 投注时比分
     */
    private String betScore;

    /**
     * forecast预测分数(1.大小玩法时,表示两队进球和. 2.让球玩法时,表示进球数量差值)
     */
    private Integer forecastScore;

    /**
     * 预测盈利(庄家视角)
     */
    private BigDecimal profitAmount;

    /**
     * 完整让球(盘口值)
     */
    private String marketValueComplete;

    /**
     * 当前让球(盘口值)
     */
    private String marketValueCurrent;

    /**
     * 创建时间
     */
    private Long createTime;



}
