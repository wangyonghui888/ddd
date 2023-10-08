package com.panda.sport.rcs.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.panda.sport.rcs.console.dao.RcsMatchPlayConfigLogsMapper;
import com.panda.sport.rcs.console.pojo.RcsMatchPlayConfigLogs;
import com.panda.sport.rcs.console.service.RcsMatchPlayConfigLogsService;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.service.impl
 * @Description :  TODO
 * @Date: 2020-02-10 15:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class RcsMatchPlayConfigLogsServiceImpl implements RcsMatchPlayConfigLogsService {
    @Autowired
    private RcsMatchPlayConfigLogsMapper rcsMatchPlayConfigLogsMapper;

    @Override
    public List<RcsMatchPlayConfigLogs> selectByMatchId(Long matchId) {
//        Map<String, Object> columnMap = new HashMap<>(1);
//        columnMap.put("matchId", matchId);
        RcsMatchPlayConfigLogs logs = new RcsMatchPlayConfigLogs();
        logs.setMatchId(matchId);
        return rcsMatchPlayConfigLogsMapper.select(logs);
    }
}
