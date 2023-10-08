package com.panda.sport.rcs.pojo;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class RcsStandardPlaceRef extends RcsBaseEntity<RcsStandardPlaceRef> {
    /**
    * 主键id
    */
    private Long id;

    /**
    * 位置id
    */
    private String placeId;

    /**
    * 标准比赛ID   standard_match_info.id
    */
    private Long standardMatchInfoId;

    /**
    * 标准玩法id   standard_sport_market_category.id
    */
    private Long marketCategoryId;

    /**
    * 盘口位置，1：表示主盘，2：表示第一副盘
    */
    private Integer placeNum;

    /**
    * 盘口id
    */
    private Long marketId;

    /**
    * 版本号，查询的时候使用位置1的版本号做关联
    */
    private String versionId;

    private Date createTime;

    /**
    * 修改时间  
    */
    private Date modifyTime;

    /**
     * 子玩法id
     */
    private String childMarketCategoryId;
}