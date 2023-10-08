package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.MatchStatisticsInfoDetailFlowing;
import org.apache.ibatis.annotations.Param;
import tk.mapper.MyMapper;

import java.util.List;

public interface MatchStatisticsInfoDetailFlowingMapper  extends MyMapper<MatchStatisticsInfoDetailFlowing> {

    int batchInsert(@Param("list") List<MatchStatisticsInfoDetailFlowing> list);

}