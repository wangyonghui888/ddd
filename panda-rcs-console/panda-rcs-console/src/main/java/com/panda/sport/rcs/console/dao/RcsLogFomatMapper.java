package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.RcsLogFomat;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mapper.MyMapper;

import java.util.List;

@Repository
public interface RcsLogFomatMapper extends MyMapper<RcsLogFomat> {
    int updateBatch(List<RcsLogFomat> list);

    int batchInsert(@Param("list") List<RcsLogFomat> list);

    int insertOrUpdate(RcsLogFomat record);

    int insertOrUpdateSelective(RcsLogFomat record);

    List<RcsLogFomat> getRcsLogFomats(@Param("bean")MatchFlowingDTO matchFlowingDTO);
}