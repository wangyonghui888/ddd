package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetailSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchStatisticsInfoDetailSourceMapper extends BaseMapper<MatchStatisticsInfoDetailSource> {

    int insertOrUpdate(MatchStatisticsInfoDetailSource record);

    int insertOrUpdateSelective(MatchStatisticsInfoDetailSource record);

    int insertSelective(MatchStatisticsInfoDetailSource record);

    int batchInsert(@Param("list") List<MatchStatisticsInfoDetailSource> list);

    int batchInsertOrUpdate(@Param("list") List<MatchStatisticsInfoDetailSource> matchStatisticsInfoDetailList);
}