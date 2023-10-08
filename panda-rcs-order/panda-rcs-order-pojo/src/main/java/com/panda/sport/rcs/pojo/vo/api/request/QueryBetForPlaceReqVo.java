package com.panda.sport.rcs.pojo.vo.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 投注项/坑位-期望值/货量 reqVo
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryBetForPlaceReqVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 体育种类
     */
    private Integer sportId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Integer playId;

    /**
     * 1.早盘  2.滚球
     */
    private Integer matchType;

    /**
     * 当前比分
     */
    private String score;

    /**
     * 类型  1玩法级别 2坑位级别
     */
    private Integer dataType;
}
