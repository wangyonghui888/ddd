package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardSportOddsFieldsTemplet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName StandardSportOddsFieldsTempletMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/4
 **/
@Mapper
public interface StandardSportOddsFieldsTempletMapper extends BaseMapper<StandardSportOddsFieldsTemplet> {


    int batchInsert(@Param("list") List<StandardSportOddsFieldsTemplet> list);

    int batchInsertOrUpdate(@Param("list") ArrayList<StandardSportOddsFieldsTemplet> standardSportOddsFieldsTemplets);
}
