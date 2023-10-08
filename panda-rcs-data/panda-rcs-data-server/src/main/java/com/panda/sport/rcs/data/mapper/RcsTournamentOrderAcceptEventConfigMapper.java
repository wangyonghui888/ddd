package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsTournamentOrderAcceptEventConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RcsTournamentOrderAcceptEventConfigMapper extends BaseMapper<RcsTournamentOrderAcceptEventConfig> {
    int updateBatch(List<RcsTournamentOrderAcceptEventConfig> list);

    int batchInsert(@Param("list") List<RcsTournamentOrderAcceptEventConfig> list);

    int insertOrUpdate(RcsTournamentOrderAcceptEventConfig record);

    int insertOrUpdateSelective(RcsTournamentOrderAcceptEventConfig record);
}