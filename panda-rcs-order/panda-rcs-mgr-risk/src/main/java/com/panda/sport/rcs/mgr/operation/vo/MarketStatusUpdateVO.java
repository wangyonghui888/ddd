package com.panda.sport.rcs.mgr.operation.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.vo
 * @Description : 手动/自动切换，开/关/封/锁请求入参
 * @Author : Paca
 * @Date : 2020-07-16 11:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@Accessors(chain = true)
public class MarketStatusUpdateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操盘级别，1-赛事级别，2-玩法级别，3-盘口级别，4-玩法集
     *
     * @see com.panda.sport.rcs.enums.TraderLevelEnum
     */
    private Integer tradeLevel;

    /**
     * 运动种类ID
     */
    private Long sportId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Long categoryId;

    /**
     * 盘口ID
     */
    private String marketId;

    /**
     * 盘口位置
     */
    private Integer marketPlaceNum;

    /**
     * 玩法集ID
     */
    private Long categorySetId;

    /**
     * 玩法ID集合
     */
    private List<Long> categoryIdList;

    /**
     * 状态，0-开，1-封，2-关，11-锁
     *
     * @see com.panda.sport.rcs.enums.MarketStatusEnum
     */
    private Integer marketStatus;

    /**
     * 操盘类型，0-自动操盘，1-手动操盘
     *
     * @see com.panda.sport.rcs.enums.TradeTypeEnum
     */
    private Integer tradeType;


}
