package com.panda.sport.rcs.console.service;

import java.util.List;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.MatchEventInfoFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;

public interface MatchEventInfoFlowingService {


    int updateBatch(List<MatchEventInfoFlowing> list);

    int batchInsert(List<MatchEventInfoFlowing> list);

    int insertOrUpdate(MatchEventInfoFlowing record);

    int insertOrUpdateSelective(MatchEventInfoFlowing record);

    PageDataResult getEventList(MatchFlowingDTO userSearch, Integer pageNum, Integer pageSize);
}

