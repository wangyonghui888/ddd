package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.MatchStatisticsInfoFlowing;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mapper.MyMapper;

import java.util.List;
@Repository
public interface MatchStatisticsInfoFlowingMapper extends MyMapper<MatchStatisticsInfoFlowing> {


    int insertOrUpdate(MatchStatisticsInfoFlowing record);

    int insert(MatchStatisticsInfoFlowing record);
}