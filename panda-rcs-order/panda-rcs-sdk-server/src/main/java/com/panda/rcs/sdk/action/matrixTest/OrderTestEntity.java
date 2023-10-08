package com.panda.rcs.sdk.action.matrixTest;

import lombok.Data;

/**
 * @author :  dorich
 * @project Name :  rcs-parent
 * @package Name :  com.panda.rcs.sdk.action.matrixTest
 * @description :  TODO
 * @date: 2020-04-04 9:21
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class OrderTestEntity {


    /**
     * 玩法id  -1表示其他玩法
     */
    private Integer playId =  1 ;
    /**
     * 投注项
     */
    private String playOptions = "1";

    /*** 盘口值  ***/
    private String marketValueNew;

    /*** 盘口值  ***/
    private String marketValue;

    /*** 基准分  ***/
    private String scoreBenchmark;




    
}
