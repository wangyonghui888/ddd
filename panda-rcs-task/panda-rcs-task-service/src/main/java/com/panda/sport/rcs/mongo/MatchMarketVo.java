package com.panda.sport.rcs.mongo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author :  Administrator
 * @Project Name :  panda-rcs-task-group
 * @Package Name :  com.panda.sport.rcs.mongo
 * @Description :  TODO
 * @Date: 2020-07-19 9:12
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchMarketVo {

    @Field(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 盘口id
     */
    private Long marketCategoryId;
    private Long marketId;
    /**
     * 盘口位置
     */
    private Integer marketIndex;
    /**
     *
     * 多语言
     */
    private Map<String, String> names;
    private Long nameCode;
    /**
     * 平衡值
     */
    private Long balance;

    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    private Integer marketType;

    /**
     * 水差
     */
    private BigDecimal waterValue;

    private Integer diffOdds;

    private Integer minOdds;

    private Double sortHandle = 0d;

    private BigDecimal marginValue;
    /**
     * 盘口是否关联 1关联
     */
    private Integer relevanceType;
    /**
     * 盘口位置id
     */
    private String placeNumId;

    /**
     * 盘口差
     */
    private Double marketHeadGap;

    private String addition1;

    private String addition2;

    private String addition3;

    private String addition4;

    private String addition5;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;
    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String internalDataSourceCode;

    private Long updateTime;
    
    private Integer changeRateStatus;
    
    private String extraInfo;

    /**
     * 三方盘口源状态,给风控操盘使用，融合侧不做修改
     */
    private Integer thirdMarketSourceStatus;
    /**
     * 旧三方盘口源状态,给风控操盘使用，融合侧不做修改
     */
    private Integer oldThirdMarketSourceStatus;
    /**
     * 通过以上三种状态加上操盘赛事状态得出的最终状态
     * 盘口状态0-5. 0:active 开盘, 1:suspended 封盘, 2:deactivated 关盘, 3:settled 已结算, 4:cancelled 已取消, 5:handedOver  盘口的中间状态，该状态的盘口后续不会有赔率过来 11:锁盘状态
     */
    private Integer status;

    /**
     * 操盘后台设置的位置状态，融合侧不做修改，可为空（操盘后台没有设置位置状态为空）
     */
    private Integer placeNumStatus;
    /**
     * pa状态，赔率合法性校验，最大最小值校验设置的状态，可为空(没有经过赔率合法性校验为空)
     */
    private Integer paStatus;

    private String paStatusReason;

    /**
     * 位置
     */
    private Integer placeNum;
    /**
     * 盘口赔率
     */
    private List<MatchMarketOddsVo> oddsFieldsList;
    
    private String oddsName;

    /**
     * 子玩法id
     */
    private String childMarketCategoryId;

    /**
     * 盘口来源 0：数据商 1：融合构建
     */
    private Integer marketSource;

    /**
     * 收盘状态
     */
    private Integer endEdStatus;

    /**
     * 1累封 2防封
     */
    private Integer placeNumStatusDisplay;

}
