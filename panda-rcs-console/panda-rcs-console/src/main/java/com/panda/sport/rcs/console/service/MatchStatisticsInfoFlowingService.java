package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.MatchStatisticsInfoFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;

import java.util.List;

public interface MatchStatisticsInfoFlowingService {

    int insertOrUpdate(MatchStatisticsInfoFlowing record);

    PageDataResult getStatisticsList(MatchFlowingDTO userSearch, Integer pageNum, Integer pageSize);

    PageDataResult getStatisticsDetailList(MatchFlowingDTO matchFlowingDTO, Integer pageNum, Integer pageSize);
}

