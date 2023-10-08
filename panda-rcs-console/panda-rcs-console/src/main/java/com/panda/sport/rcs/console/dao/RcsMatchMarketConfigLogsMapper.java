package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.RcsMatchMarketConfigLogs;
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
 * @Date: 2020-02-10 15:30
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface RcsMatchMarketConfigLogsMapper extends BaseMapper<RcsMatchMarketConfigLogs> {

    List<Map> selectById(@Param("matchId") Integer matchId, @Param("marketId") Long marketId);
}
