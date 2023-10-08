package com.panda.sport.rcs.data.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardSportPlayer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName StandardSportPlayerMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/4
 **/
@Mapper
public interface StandardSportPlayerMapper extends BaseMapper<StandardSportPlayer> {

    int updateBatch(List<StandardSportPlayer> list);

    int batchInsert(@Param("list") List<StandardSportPlayer> list);
}