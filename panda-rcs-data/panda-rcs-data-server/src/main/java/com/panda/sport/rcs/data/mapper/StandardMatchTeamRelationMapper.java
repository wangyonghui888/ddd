package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardMatchTeamRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName StandardMatchTeamRelationMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/4
 **/
@Mapper
public interface StandardMatchTeamRelationMapper extends BaseMapper<StandardMatchTeamRelation> {

    int updateBatch(List<StandardMatchTeamRelation> list);

    int batchInsert(@Param("list") List<StandardMatchTeamRelation> list);

    String selectMatchPosition(Long id);

    int batchInsertOrUpdate(@Param("list") List<StandardMatchTeamRelation> list);
}