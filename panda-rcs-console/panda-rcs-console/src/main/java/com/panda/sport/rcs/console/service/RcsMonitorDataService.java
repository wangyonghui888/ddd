package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.RcsMonitorData;
import com.panda.sport.rcs.console.pojo.RcsMonitorDataVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RcsMonitorDataService {


    int updateBatch(List<RcsMonitorData> list);

    int updateBatchSelective(List<RcsMonitorData> list);

    int batchInsert(List<RcsMonitorData> list);

    int insertOrUpdate(RcsMonitorData record);

    int insertOrUpdateSelective(RcsMonitorData record);

    List<RcsMonitorDataVo> queryRate(MatchFlowingDTO bean);

    List group();

    HashMap<String, Map> graphaData(MatchFlowingDTO matchFlowingDTO);
}
