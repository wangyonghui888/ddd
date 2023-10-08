package com.panda.sport.rcs.vo;

import lombok.Data;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  TODO
 * @Date: 2020-02-19 13:49
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchStatusAndDataSuorceVo {
    /**
     * 赛事Id
     */
    private Long matchId;
    /**
     * 等级  1赛事  2玩法 3盘口  4玩法阶段
     */
    private Integer level;
    /**
     * Id
     */
    private String id;
    /**
     * 操盘类型  0是自动 1是手动
     */
    private Integer dataSource;
    /**
     * 操盘类型 开关封锁
     */
    private Integer status;
    
    /**
     * 操盘平台 MTS ，PA
     */
    private String riskManagerCode;
}
