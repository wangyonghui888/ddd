package com.panda.sport.rcs.data.service;

import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;

import java.util.List;

public interface MatchStatisticsInfoDetailService {


    int deleteByPrimaryKey(Long id);

/*
    int insert(MatchStatisticsInfoDetail record);
*/

    int insertOrUpdate(MatchStatisticsInfoDetail record);

    int insertOrUpdateSelective(MatchStatisticsInfoDetail record);

    int insertSelective(MatchStatisticsInfoDetail record);

    MatchStatisticsInfoDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MatchStatisticsInfoDetail record);

    int updateByPrimaryKey(MatchStatisticsInfoDetail record);

/*
    int updateBatch(List<MatchStatisticsInfoDetail> list);
*/

    int batchInsert(List<MatchStatisticsInfoDetail> list);

    int batchInsertOrUpdate(List<MatchStatisticsInfoDetail> matchStatisticsInfoDetailList);

 	/**
     * 查找赛事统计详情
     **/
    List<MatchStatisticsInfoDetail> queryStatisticsInfoDetailsByMatchId(Long standardMatchId);

}