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
 * 足球玩法forecast
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rcs_predict_forecast_play")
public class RcsPredictForecastPlay implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 1：早盘 2滚球
     */
    private Integer matchType;

    /**
     * 类型  1玩法级别 2坑位级别
     */
    private Integer dataType;

    /**
     * 玩法ID
     */
    private Long playId;

    /**
     * 坑位 data_type为2的时候才有数据
     */
    private Integer placeNum;

    /**
     * 比分-12到12之间
     */
    private Integer score;

    /**
     * 期望值
     */
    private BigDecimal profitValue;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;


}
