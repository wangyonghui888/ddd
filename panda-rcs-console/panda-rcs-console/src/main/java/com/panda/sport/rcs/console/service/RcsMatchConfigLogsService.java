package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.pojo.ExcelVO;
import com.panda.sport.rcs.console.response.PageDataResult;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.service
 * @Description :  TODO
 * @Date: 2020-02-10 15:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsMatchConfigLogsService {
    PageDataResult getStatusList(Integer matchId, Integer pageNum, Integer pageSize) throws ParseException;

	PageDataResult queryTradeLogList(Map<String, Object> params);

	void insertUserConfig(List<ExcelVO> collect, CountDownLatch countDownLatch);

	List<String> getUserBetRate();

	int deleteUserBetRate();

}
