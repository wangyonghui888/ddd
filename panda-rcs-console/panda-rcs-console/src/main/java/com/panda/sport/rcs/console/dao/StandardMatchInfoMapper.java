package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.StandardMatchInfo;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.console.dao
 * @Description :  TODO
 * @Date: 2020-07-29 16:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface StandardMatchInfoMapper extends BaseMapper<StandardMatchInfo> {
    List<Map<String, Object>> selectStandardMatchInfoById(Long matchId);


    List<Map<String, Object>> selectStandardSportMarket(Long matchId, Long marketId);

    List<Map<String, Object>> selectStandardSportMarketOdds(Long marketId, Long oddId);
}
