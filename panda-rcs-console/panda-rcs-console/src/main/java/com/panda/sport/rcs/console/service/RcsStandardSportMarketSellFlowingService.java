package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.RcsStandardSportMarketSellFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;

import java.util.List;

public interface RcsStandardSportMarketSellFlowingService {


    int updateBatch(List<RcsStandardSportMarketSellFlowing> list);

    int batchInsert(List<RcsStandardSportMarketSellFlowing> list);

    int insertOrUpdate(RcsStandardSportMarketSellFlowing record);

    int insertOrUpdateSelective(RcsStandardSportMarketSellFlowing record);

    PageDataResult getOpenSellList(MatchFlowingDTO matchFlowingDTO, Integer pageNum, Integer pageSize);
}
