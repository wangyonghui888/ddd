package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.pojo.RcsMatchPlayConfigLogs;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.service
 * @Description :  TODO
 * @Date: 2020-02-10 15:34
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsMatchPlayConfigLogsService {
    /**
     * @return java.util.List<com.panda.sport.rcs.console.pojo.RcsMatchPlayConfigLogs>
     * @Description //根据赛事id查询记录
     * @Param [matchId]
     * @Author kimi
     * @Date 2020/2/10
     **/
    List<RcsMatchPlayConfigLogs> selectByMatchId(Long matchId);
}
