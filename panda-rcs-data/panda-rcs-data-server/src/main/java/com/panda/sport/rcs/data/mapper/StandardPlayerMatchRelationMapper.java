package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardPlayerMatchRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName StandardPlayerMatchRelationMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/4
 **/
@Mapper
public interface StandardPlayerMatchRelationMapper extends BaseMapper<StandardPlayerMatchRelation> {


    int updateBatch(List<StandardPlayerMatchRelation> list);

    int batchInsert(@Param("list") List<StandardPlayerMatchRelation> list);
}