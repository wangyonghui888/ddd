package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface MatchStatisticsInfoDetailMapper extends BaseMapper<MatchStatisticsInfoDetail> {
    int deleteByPrimaryKey(Long id);

    int insertOrUpdate(MatchStatisticsInfoDetail record);

    int insertOrUpdateSelective(MatchStatisticsInfoDetail record);

    int insertSelective(MatchStatisticsInfoDetail record);

    MatchStatisticsInfoDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MatchStatisticsInfoDetail record);

    int updateByPrimaryKey(MatchStatisticsInfoDetail record);

    int batchInsert(@Param("list") List<MatchStatisticsInfoDetail> list);

    int batchInsertOrUpdate(@Param("list") List<MatchStatisticsInfoDetail> list);

	List<MatchStatisticsInfoDetail> queryStatisticsInfoDetailsByMatchId(@Param("id")Long standardMatchId);}