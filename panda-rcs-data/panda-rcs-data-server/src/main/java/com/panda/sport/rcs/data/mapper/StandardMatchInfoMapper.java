package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @ClassName StandardMatchInfoMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/4
 **/
@Mapper
public interface StandardMatchInfoMapper extends BaseMapper<StandardMatchInfo> {

    int updateBatch(List<StandardMatchInfo> list);

    int batchInsertOrUpdate(@Param("list") List<StandardMatchInfo> list);

    /**
     * @MethodName: getLastCrtTime
     * @Description: 得到最后的插入时间
     * @Param: []
     * @Return: java.lang.Long
     * @Author: Vector
     * @Date: 2019/9/28
     **/
    @Select("SELECT max(modify_time) from standard_match_info")
    Long getLastCrtTime();

    int updateOperateMatchStatus(@Param("id") Long standardMatchInfoId, @Param("status")Integer operateMatchStatus);

    StandardMatchInfo selectMatchById(@Param("matchId") Long matchId);
}