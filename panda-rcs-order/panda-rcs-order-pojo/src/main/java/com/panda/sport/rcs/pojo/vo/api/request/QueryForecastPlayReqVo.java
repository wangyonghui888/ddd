package com.panda.sport.rcs.pojo.vo.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 玩法级别、坑位级别的 Forecast reqVo
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryForecastPlayReqVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 1：早盘 2滚球
     */
    private Integer matchType;

    /**
     * 玩法ID
     */
    private Long playId;

    /**
     * 类型  1玩法级别 2坑位级别
     */
    private Integer dataType;

    /**
     * 比分-12到12之间
     */
    private Integer score;

    /**
     * 预约投注类型 0、正常类型 1、预约投注类型
     */
    private Integer pendingType = 0;

    private String hashUnique;
}
