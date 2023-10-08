package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchOrderAcceptConfig;
import com.panda.sport.rcs.pojo.RcsMatchOrderAcceptEventConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RcsMatchOrderAcceptEventConfigMapper extends BaseMapper<RcsMatchOrderAcceptEventConfig> {
    int updateBatch(List<RcsMatchOrderAcceptEventConfig> list);

    int batchInsert(@Param("list") List<RcsMatchOrderAcceptEventConfig> list);

    int insertOrUpdate(RcsMatchOrderAcceptEventConfig record);

    int insertOrUpdateSelective(RcsMatchOrderAcceptEventConfig record);

    List<RcsMatchOrderAcceptConfig> queryMatchOrderConfig(@Param("matchId") Long matchId,@Param("tournamentId") Long tournamentId);
}