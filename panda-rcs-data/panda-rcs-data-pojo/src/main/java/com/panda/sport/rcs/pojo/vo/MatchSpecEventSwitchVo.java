package com.panda.sport.rcs.pojo.vo;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 特殊事件通知前端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchSpecEventSwitchVo extends RcsBaseEntity<MatchSpecEventSwitchVo> {

    /**
     * 切换特殊事件
     */
    public static final int TYPE_CHANGE_EVENT = 1;
    /**
     * 特殊事件修改赔率
     */
    public static final int TYPE_CHANGE_ODDS = 2;
    /**
     * 退出特殊事件
     */
    public static final int TYPE_EXIT_SPEC = 3;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 通知类型
     */
    private Integer type;

    /**
     * 特殊事件code
     */
    private String eventCode;
    /**
     * 特殊事件名称
     */
    private String eventName;
    /**
     * 上一次特殊事件code
     */
    private String lastEventCode;
    /**
     * 上一次特殊事件名称
     */
    private String lastEventName;

    /**
     * homeGoalProb
     */
    private Float homeGoalProb;

    /**
     * 当前事件主客 home:主队 away:客队
     */
    private String currHomeAway;

    /**
     * 上一次事件主客 home:主队 away:客队
     */
    private String lastHomeAway;
}
