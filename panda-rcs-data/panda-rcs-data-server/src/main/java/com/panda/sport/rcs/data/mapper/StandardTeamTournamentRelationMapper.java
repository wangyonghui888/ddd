package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardTeamTournamentRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName StandardTeamTournamentRelationMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/4
 **/
@Mapper
public interface StandardTeamTournamentRelationMapper extends BaseMapper<StandardTeamTournamentRelation> {

    int updateBatch(List<StandardTeamTournamentRelation> list);

    int batchInsert(@Param("list") List<StandardTeamTournamentRelation> list);
}