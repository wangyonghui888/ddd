package com.panda.sport.rcs.wrapper;

import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import org.apache.ibatis.annotations.Param;

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


    String queryMatchScore( Long matchId);


}