package com.panda.sport.rcs.pojo.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PredictOddsMqVo {
    /**
     * 唯一请求id
     */
    String linkId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Integer playId;


    /**
     * 1.早盘  2.滚球
     */
    private Integer matchType;

    /**
     * 类型  1投注项 2坑位
     */
    private Integer dataType;

    /**
     * 盘口ID /坑位ID
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dataTypeValue;

    /**
     * 货量清零 1是 0否
     */
    private Integer clearZero;
    /**
     * 盘口货量
     */
    List<RcsPredictBetOddsVo> list;

    /**
     * 子玩法ID
     */
    private String subPlayId;
    /**
     * 单关/串关 1单关 2串关
     */
    private Integer seriesType;

}
