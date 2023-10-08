package com.panda.sport.rcs.console.service;

import java.util.List;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.MatchStatusFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;

public interface MatchStatusFlowingService {


    int updateBatch(List<MatchStatusFlowing> list);

    int batchInsert(List<MatchStatusFlowing> list);

    int insertOrUpdate(MatchStatusFlowing record);

    int insertOrUpdateSelective(MatchStatusFlowing record);

    PageDataResult getStatusList(MatchFlowingDTO userSearch, Integer pageNum, Integer pageSize);
}

