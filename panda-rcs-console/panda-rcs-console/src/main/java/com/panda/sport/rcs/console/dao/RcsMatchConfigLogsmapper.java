package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.ExcelVO;
import com.panda.sport.rcs.console.pojo.RcsMatchConfigLogs;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.dao
 * @Description :  TODO
 * @Date: 2020-02-10 15:31
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface RcsMatchConfigLogsmapper extends BaseMapper<RcsMatchConfigLogs> {
    List<Map> selectById(@Param("matchId") Integer matchId);

	Map<String, Object> queryMarketInfo(Map<String, Object> params);

	List<Map<String, Object>> queryTradeList(Map<String, Object> params);

	int batchUpdateUserConfig(List<ExcelVO> list);
	int batchAddOrUpdateUserConfig(List<ExcelVO> list);
	List<String> getUserBetRate();
	int deleteUserBetRate();

}
