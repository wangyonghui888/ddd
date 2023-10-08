package com.panda.sport.rcs.pojo;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.util.Date;

/**
    * 盘口表冠军附加表
    */
@Data
public class RcsMarketChampionExt extends RcsBaseEntity<RcsMarketChampionExt> {
    /**
     * 数据库id, 自增
     */
    private Long id;

    /**
     * 运动种类id.  对应表 sport.id
     */
    private Long sportId;

    /**
     * 标准比赛ID   standard_match_info.id
     */
    private Long standardMatchInfoId;

    /**
     * 标准玩法id   standard_sport_market_category.id
     */
    private Long marketCategoryId;

    /**
     * 盘口ID  standard_sport_market.id
     */
    private Long marketId;

    /**
     * 下次封盘时间
     */
    private String nextSealTime;

    /**
     * 盘口开始时间
     */
    private String marketStartTime;

    /**
     * 盘口结束时间
     */
    private String marketEndTime;

    private Date createTime;

    private Date updateTime;
}