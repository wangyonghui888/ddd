//package com.panda.sport.rcs.oddin.entity.common.pojo;
//
//import lombok.Data;
//
//import java.io.Serializable;
//import java.util.List;
//
///**
// * 标准盘口vo
// */
//@Data
//public class StandardMarketVo implements Serializable {
//    private static final long serialVersionUID = 1L;
//    /**
//     * 赛事id
//     */
//    private Long matchId;
//
//    /**
//     * 标准盘口id
//     * 非空
//     */
//    private Long id;
//    /**
//     * 三方盘口源状态,给风控操盘使用，融合侧不做修改
//     */
//    private Integer thirdMarketSourceStatus;
//    /**
//     * 通过以上三种状态加上操盘赛事状态得出的最终状态
//     * 盘口状态0-5. 0:active 开盘, 1:suspended 封盘, 2:deactivated 关盘, 3:settled 已结算, 4:cancelled 已取消, 5:handedOver  盘口的中间状态，该状态的盘口后续不会有赔率过来 11:锁盘状态
//     */
//    private Integer status;
//
//
//    /**
//     * 非空
//     * 标准玩法id   standard_sport_market_category.id
//     */
//    private Long marketCategoryId;
//
//    /**
//     * 子玩法id
//     */
//    private Long childMarketCategoryId;
//
//    /**
//     * 非空
//     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
//     */
//    private Integer marketType;
//
//    /**
//     * 盘口位置
//     */
//    private Integer placeNum;
//
//
//    /**
//     * 盘口投注项
//     */
//    private List<StandardMarketOddsVo> marketOddsList;
//
//    /**
//     * 附加字段5
//     */
//    private String addition5;
//
//    private String selectionId;
//}