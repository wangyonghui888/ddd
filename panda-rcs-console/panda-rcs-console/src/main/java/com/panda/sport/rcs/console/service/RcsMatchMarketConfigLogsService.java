package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.response.PageDataResult;

import java.text.ParseException;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.service
 * @Description :  TODO
 * @Date: 2020-02-10 15:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsMatchMarketConfigLogsService {

    PageDataResult getStatusList(MatchFlowingDTO userSearch, Integer pageNum, Integer pageSize);

    PageDataResult getStatusList(Integer matchId, Long marketId, Integer pageNum, Integer pageSize) throws ParseException;
}
