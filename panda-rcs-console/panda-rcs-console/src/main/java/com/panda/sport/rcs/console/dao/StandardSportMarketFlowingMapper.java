package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.StandardSportMarketFlowing;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mapper.MyMapper;

import java.util.List;
@Repository
public interface StandardSportMarketFlowingMapper extends MyMapper<StandardSportMarketFlowing> {
    int updateBatch(List<StandardSportMarketFlowing> list);

    int batchInsert(@Param("list") List<StandardSportMarketFlowing> list);

    int insertOrUpdate(StandardSportMarketFlowing record);

    int insertOrUpdateSelective(StandardSportMarketFlowing record);
}