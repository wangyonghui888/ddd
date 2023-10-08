package com.panda.sport.rcs.pojo.vo.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 预测货量表 reqVo
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryBetForMarketReqVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运动种类
     */
    private Integer sportId;

    /**
     * 标准赛事id
     */
    private Long matchId;

    /**
     * 赛事类型:1赛前,2滚球
     */
    private Integer matchType;

    /**
     * 玩法id
     */
    private Integer playId;

    /**
     * 当前比分
     */
    private String score;

    /**
     * 预约投注类型 0、正常类型 1、预约投注类型
     */
    private Integer pendingType = 0;

    private String hashUnique;

}