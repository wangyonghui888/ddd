package com.panda.sport.rcs.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class ForecastMqVo {

    /**
     * 唯一请求id
     */
    String linkId;

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
     * forecast数据
     */
    List<PredictForecastVo> list;

}
