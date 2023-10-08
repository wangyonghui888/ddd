package com.panda.sport.rcs.data.mapper;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardSportTeam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName StandardSportTeamMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/4
 **/
@Mapper
public interface StandardSportTeamMapper extends BaseMapper<StandardSportTeam> {

    int updateBatch(List<StandardSportTeam> list);

    int batchInsert(@Param("list") List<StandardSportTeam> set);

    int batchInsertOrUpdate(@Param("list") List<StandardSportTeam> standardSportTeams);
}