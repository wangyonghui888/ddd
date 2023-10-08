package com.panda.sport.rcs.pojo.param;

import lombok.Data;

/**
 * 业务逻辑
 *
 * @author gray
 * @version 1.0.0
 * @date 2023/4/10 21:24
 */
@Data
public class RcsSpecEventConfigParam {
    /**
     * 主键
     */
    private Integer id;
    
    /**
     * 1：级别  2：联赛id   3：赛事id
     */
    private Integer type;
    
    /**
     * 根据type设置（1：设值联赛等级  2：设值联赛id   3：设值赛事id）
     */
    private Long typeVal;
    
    /**
     * 事件级开关 1-开；0-关
     */
    private Integer eventSwitch;
    
    /**
     * 单边抽水开关 1-开；0-关
     */
    private Integer oneSideSwitch;
    
    /**
     * 客队-进球默认的概率
     */
    private Float awayGoalProb;
    
    /**
     * 主队-进球默认的概率
     */
    private Float homeGoalProb;
}
