package com.panda.sport.rcs.pojo.vo.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 10001 常规
 * 10002 角球
 * 10003 加时
 * 1004 加时点球
 *
 * @author author
 * @since 2021-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MatchScoreReqVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 10001 常规
     * 10002 角球
     * 10003 加时
     * 1004 加时点球
     */
    private Integer categorySetId;


}
