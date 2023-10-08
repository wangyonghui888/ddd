package com.panda.sport.rcs.console.service;

import java.util.List;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.StandardSportMarketOddsFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;

public interface StandardSportMarketOddsFlowingService {


    int updateBatch(List<StandardSportMarketOddsFlowing> list);

    int batchInsert(List<StandardSportMarketOddsFlowing> list);

    int insertOrUpdate(StandardSportMarketOddsFlowing record);

    int insertOrUpdateSelective(StandardSportMarketOddsFlowing record);

    PageDataResult getMarketOddsList(MatchFlowingDTO userSearch, Integer pageNum, Integer pageSize);

    List getMarketOddsByParam(MatchFlowingDTO matchFlowingDTO);

}



