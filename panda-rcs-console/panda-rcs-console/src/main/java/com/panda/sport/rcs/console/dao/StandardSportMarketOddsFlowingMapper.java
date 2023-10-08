package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.dto.PermissionDTO;
import com.panda.sport.rcs.console.pojo.StandardSportMarketFlowing;
import com.panda.sport.rcs.console.pojo.StandardSportMarketOddsFlowing;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mapper.MyMapper;

import java.util.List;
@Repository
public interface StandardSportMarketOddsFlowingMapper extends MyMapper<StandardSportMarketOddsFlowing> {
    int updateBatch(List<StandardSportMarketOddsFlowing> list);

    int batchInsert(@Param("list") List<StandardSportMarketOddsFlowing> list);

    int insertOrUpdate(StandardSportMarketOddsFlowing record);

    int insertOrUpdateSelective(StandardSportMarketOddsFlowing record);

}