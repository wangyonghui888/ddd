package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.MatchStatusFlowing;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mapper.MyMapper;

import java.util.List;
@Repository
public interface MatchStatusFlowingMapper extends MyMapper<MatchStatusFlowing> {
    int updateBatch(List<MatchStatusFlowing> list);

    int batchInsert(@Param("list") List<MatchStatusFlowing> list);

    int insertOrUpdate(MatchStatusFlowing record);

    int insertOrUpdateSelective(MatchStatusFlowing record);
}