package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName StandardSportMarketCategoryMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/4
 **/
@Mapper
public interface StandardSportMarketCategoryMapper extends BaseMapper<StandardSportMarketCategory> {

    int batchInsert(@Param("list") List<StandardSportMarketCategory> list);

    /**
     * @MethodName: getLastCrtTime
     * @Description: 得到最后的插入时间
     * @Param: []
     * @Return: java.lang.Long
     * @Author: Vector
     * @Date: 2019/9/28
     **/
    @Select("SELECT max(modify_time) from standard_sport_market_category")
    Long getLastCrtTime();

    int batchInsertOrUpdate(@Param("list") List<StandardSportMarketCategory> standardSportMarketCategories);

	int batchInsertOrUpdateCategoryRef(@Param("list") ArrayList<Map<String, Object>> categoryRefList);
}