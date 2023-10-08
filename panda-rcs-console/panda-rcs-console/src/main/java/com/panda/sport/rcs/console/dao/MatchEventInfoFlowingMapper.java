package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.MatchEventInfoFlowing;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mapper.MyMapper;

import java.util.List;
@Repository
public interface MatchEventInfoFlowingMapper extends MyMapper<MatchEventInfoFlowing> {
    int updateBatch(List<MatchEventInfoFlowing> list);

    int batchInsert(@Param("list") List<MatchEventInfoFlowing> list);

    int insertOrUpdate(MatchEventInfoFlowing record);

    int insertOrUpdateSelective(MatchEventInfoFlowing record);

}