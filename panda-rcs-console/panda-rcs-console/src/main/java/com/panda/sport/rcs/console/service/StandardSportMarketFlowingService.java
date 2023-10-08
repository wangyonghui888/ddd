package com.panda.sport.rcs.console.service;

import java.util.List;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.StandardSportMarketFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;

public interface StandardSportMarketFlowingService{


    int updateBatch(List<StandardSportMarketFlowing> list);

    int batchInsert(List<StandardSportMarketFlowing> list);

    int insertOrUpdate(StandardSportMarketFlowing record);

    int insertOrUpdateSelective(StandardSportMarketFlowing record);

    PageDataResult getMarketList(MatchFlowingDTO userSearch, Integer pageNum, Integer pageSize);
}
