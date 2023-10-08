package com.panda.sport.rcs.task.job.operation;

import lombok.Data;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.job.operation
 * @Description :  期望值
 * @Date: 2020-02-08 17:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ProfitRectangleVo {
    /**
     * @Description   赛事id
     * @Param 
     * @Author  toney
     * @Date  18:21 2020/2/8
     * @return 
     **/
    private Long matchId;
    /**
     * @Description   赛事类型
     * @Param 
     * @Author  toney
     * @Date  18:21 2020/2/8
     * @return 
     **/
    private Integer matchType;
    /**
     * @Description   玩法id
     * @Param 
     * @Author  toney
     * @Date  18:21 2020/2/8
     * @return 
     **/
    private Integer playId;
    /**
     * @Description   盘口id
     * @Param 
     * @Author  toney
     * @Date  18:22 2020/2/8
     * @return 
     **/
    private Long marketId;

    /**
     * @Description   /基准分
     * @Param
     * @Author  toney
     * @Date  16:32 2020/3/3
     * @return
     **/
    private String scoreBenchmark;
}
