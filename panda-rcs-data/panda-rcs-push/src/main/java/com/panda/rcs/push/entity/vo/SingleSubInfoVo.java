package com.panda.rcs.push.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SingleSubInfoVo implements Serializable {

    /**
     * 标准玩法数组
     */
    private Integer[] marketCategoryIds;

    private Integer[] states;

    /**
     * 标准赛事Id
     */
    private Long matchId;

    /**
     * 球种Id
     */
    private String sportId;

    /**
     * 语言类型
     */
    private String languageType;

    /**
     * 标准赛事Id数组
     */
    private String[] matchIds;

    /**
     * 赛事类型  0，早盘  1、滚球
     */
    private Integer matchType;

    private Integer[] playIds;

    private String userId;

    private String dataSourceCode;

    private Integer betAmount;

    private Integer[] tournamentIds;

    private Integer[] merchantIds;

    private Integer[] userLevels;

    private Integer[] riskPlayIds;

    /**
     * 1、单关 2、串关
     */
    private Integer passType;

    private Integer[] playTimeStages;

    private List<MessageDataVo> warningMessageDataList;

    private Integer[] commands;

    /**
     * 预约投注标记
     */
    private Integer pendingOrderMark;

    /**
     * 设备类型  1、H5 2、PC 3、App 4、Other
     */
    private Integer[] deviceType;

}
