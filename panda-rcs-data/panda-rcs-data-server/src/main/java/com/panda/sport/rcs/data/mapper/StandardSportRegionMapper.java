package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardSportRegion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 标准体育区域表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-26
 */
@Mapper
public interface StandardSportRegionMapper extends BaseMapper<StandardSportRegion> {

    /**
     * @MethodName: getLastCrtTime
     * @Description: 得到最后的插入时间
     * @Param: []
     * @Return: java.lang.Long
     * @Author: Vector
     * @Date: 2019/9/28
     **/
    @Select("SELECT max(modify_time) from standard_sport_region")
    Long getLastCrtTime();

    int batchInsert(@Param("list") List<StandardSportRegion> list);

    int batchInsertOrUpdate(@Param("list") List<StandardSportRegion> standardSportRegions);
}
