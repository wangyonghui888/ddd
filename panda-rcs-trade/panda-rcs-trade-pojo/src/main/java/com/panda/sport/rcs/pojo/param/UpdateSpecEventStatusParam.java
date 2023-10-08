package com.panda.sport.rcs.pojo.param;

import lombok.Data;

/**
 * 业务逻辑
 *
 * @author gray
 * @version 1.0.0
 * @date 2023/4/11 12:33
 */
@Data
public class UpdateSpecEventStatusParam {
    
    /**
     * 1：级别  2：联赛id   3：赛事id
     */
    private Integer type;
    
    /**
     * 根据type设置（1：设值联赛等级  2：设值联赛id   3：设值赛事id）
     */
    private Long typeVal;
    
    
    /**
     * 赛事级开关 1-开，0-关
     */
    private Long matchIdSwitch;

    private Integer id;
    
}
