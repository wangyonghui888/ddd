//package com.panda.sport.rcs.oddin.entity.common.pojo;
//
//import lombok.Data;
//
//import java.io.Serializable;
//import java.util.List;
//
//
///**
// * 标准赛事vo
// */
//@Data
//public class StandardMatchVo implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    /**
//     * 标准联赛ID
//     */
//    private Long standardTournamentId;
//
//    /**
//     * 标准比赛ID   standard_match_info.id
//     */
//    private Long standardMatchInfoId;
//
//    /**
//     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
//     */
//    private String dataSourceCode;
//
//    private Long modifyTime;
//    /**
//     * 是否展示角球玩法
//     * 默认展示 * Y：展示；N：不展示
//     */
//    private String displayCorner;
//
//    /**
//     * 是否显示罚球
//     * 默认展示 * Y：展示；N：不展示
//     */
//    private String displayPenalty;
//    /**
//     * 多盘口玩法展示盘口个数
//     */
//    private Integer displayMarketCount;
//    /**
//     * 比赛开盘标识
//     * 0:active 开盘, 1:suspended 封盘, 2:deactivated 关盘,11:锁盘状态
//     */
//    private Integer status;
//
//    /**
//     * 运动种类
//     */
//    private Long sportId;
//
//    /**
//     * 盘口列表
//     */
//    private List<StandardMarketVo> marketList;
//}
