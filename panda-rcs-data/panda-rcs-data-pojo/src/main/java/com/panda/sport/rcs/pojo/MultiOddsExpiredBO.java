package com.panda.sport.rcs.pojo;

import lombok.Data;

/**
 * 百家赔滚球过期5分钟标记类
 */
@Data
public class MultiOddsExpiredBO {


    /**
     * 标准比赛ID   standard_match_info.id
     */
    private String matchId;

    /**
     * 运动种类ID. 联赛所属体育种类id, 对应 sport.id
     */
    private Long sportId;

    /**
     * 玩法idd
     */
    private Long marketCategoryId;

    /**
     * 标准盘口id
     */
    private String relationMarketId;

    /**
     * TX坑位
     */
    private Integer offerLineId;

    /**
     * 详情见data_source
     */
    private String dataSourceCode;


    /**
     *标记时间
     */
    private Long time;
}
