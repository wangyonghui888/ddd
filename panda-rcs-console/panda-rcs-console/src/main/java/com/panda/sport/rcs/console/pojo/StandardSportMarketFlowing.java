package com.panda.sport.rcs.console.pojo;

import lombok.Data;

@Data
public class StandardSportMarketFlowing {
    /**
    * 表ID, 自增
    */
    private Long id;

    /**
    * 链路id
    */
    private String linkId;

    /**
    * 原本id
    */
    private Long oId;

    /**
    * 运动种类id.  对应表 sport.id
    */
    private Long sportId;

    /**
    * 所属联赛ID    standard_sport_tournament.id
    */
    private Long standardTournamentId;

    /**
    * 标准比赛ID   standard_match_info.id
    */
    private Long standardMatchInfoId;

    /**
    * 标准玩法id   standard_sport_market_category.id
    */
    private Long marketCategoryId;

    /**
    * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
    */
    private Integer marketType;

    /**
    * 该盘口具体显示的值. 例如: 大小球中, 大小界限是:  3.5
    */
    private String oddsValue;

    /**
    * 盘口名称,V1.2统一命名规则.
    */
    private String oddsName;

    /**
    * 盘口名称编码
    */
    private Long nameCode;

    /**
    * 排序类型
    */
    private String orderType;

    /**
     * 盘口级别，数字越小优先级越高
     */
    private Integer oddsMetric;

    /**
     * 盘口位置，1：表示主盘，2：表示第一副盘
     */
    private Integer placeNum;


    /**
    * 附加字段1
    */
    private String addition1;

    /**
    * 附加字段2
    */
    private String addition2;

    /**
    * 附加字段3
    */
    private String addition3;

    /**
    * 附加字段4
    */
    private String addition4;

    /**
    * 附加字段5
    */
    private String addition5;

    /**
    * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
    */
    private String dataSourceCode;

    /**
    * 盘口状态0-5. 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver
    */
    private Integer status;

    private Integer thirdMarketSourceStatus;

    /**
     *位置状态
     */
    private Integer placeNumStatus;
    /**
     * pa状态
     */
    private Integer paStatus;
    /**
     * pa状态原因
     */
    private String paStatusReason;

    /**
    * 盘口阶段id. 对应 sport_market_scope.id
    */
    private Long scopeId;

    /**
    * 1 需要;  0 不需要
    */
    private Integer managerConfirmPrize;

    /**
    * 该字段用于做风控时, 需要替换成风控服务商提供的盘口id.  如果数据源发生切换, 当前字段需要更新.
    */
    private String thirdMarketSourceId;

    /**
    * 盘口名称多语言数组json串
    */
    private String i18nNames;

    private String remark;

    /**
    * 扩展参数：用于上游需通过融合向下游透传的参数，融合不做任何处理及存储
    */
    private String extraInfo;

    private Long createTime;

    private Long modifyTime;

    /**
    * 流水备注
    */
    private String myRemark;

    private String insertTime;

    /**
     * 盘口位置id
     */
    private String placeNumId;

    /**
     * 盘口差
     */
    private Double marketHeadGap;

    private String versionId;

    /**
     * 数据型式 0:普通  1:A+
     */
    private Integer dataType;
    /**
     * 盘口来源 0：数据商 1：融合构建
     */
    private Integer marketSource;

}
