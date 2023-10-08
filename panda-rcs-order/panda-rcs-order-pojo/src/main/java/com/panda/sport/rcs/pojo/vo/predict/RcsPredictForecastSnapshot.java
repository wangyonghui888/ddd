package com.panda.sport.rcs.pojo.vo.predict;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rcs_predict_forecast_snapshot")
public class RcsPredictForecastSnapshot implements Serializable {

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


    /**
     * 快照时间
     */
    private Long snapshotTime;

}
