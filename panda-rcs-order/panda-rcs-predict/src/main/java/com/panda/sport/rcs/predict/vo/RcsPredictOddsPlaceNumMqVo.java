package com.panda.sport.rcs.predict.vo;

import lombok.Data;

import java.util.List;

@Data
public class RcsPredictOddsPlaceNumMqVo {

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
    private Long dataTypeValue;


    /**
     * forecast数据
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

    /**
     * 投注类型(投注时下注的玩法选项)，规则引擎用
     */
    private String playOptions;

    /**
     * 盘口ID
     */
    private Long marketId;


}
