package com.panda.sport.rcs.data.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;import org.apache.ibatis.annotations.Select;

/**
 * @ClassName StandardSportTournamentMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/4
 **/
@Mapper
public interface StandardSportTournamentMapper extends BaseMapper<StandardSportTournament> {


    int batchInsert(@Param("list") List<StandardSportTournament> list);

    /**
     * @MethodName: getLastCrtTime
     * @Description: 得到最后的插入时间
     * @Param: []
     * @Return: java.lang.Long
     * @Author: Vector
     * @Date: 2019/9/28
     **/
    @Select("SELECT max(modify_time) from standard_sport_tournament")
    Long getLastCrtTime();

    int batchInsertOrUpdate(@Param("list") List<StandardSportTournament> standardSportTournaments);
}