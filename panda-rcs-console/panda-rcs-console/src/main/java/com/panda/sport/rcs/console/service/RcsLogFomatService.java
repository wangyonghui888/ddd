package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.RcsLogFomat;
import com.panda.sport.rcs.console.response.PageDataResult;

import java.util.List;

public interface RcsLogFomatService {


    int updateBatch(List<RcsLogFomat> list);

    int batchInsert(List<RcsLogFomat> list);

    int insertOrUpdate(RcsLogFomat record);

    int insertOrUpdateSelective(RcsLogFomat record);

    PageDataResult getRcsLogFomats(MatchFlowingDTO matchFlowingDTO, Integer pageNum, Integer pageSize);
}
