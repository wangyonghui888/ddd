package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.RcsStandardSportMarketSellFlowing;
import org.apache.ibatis.annotations.Param;
import tk.mapper.MyMapper;

import java.util.List;

public interface RcsStandardSportMarketSellFlowingMapper extends MyMapper<RcsStandardSportMarketSellFlowing> {
    int updateBatch(List<RcsStandardSportMarketSellFlowing> list);

    int batchInsert(@Param("list") List<RcsStandardSportMarketSellFlowing> list);

    int insertOrUpdate(RcsStandardSportMarketSellFlowing record);

    int insertOrUpdateSelective(RcsStandardSportMarketSellFlowing record);
}