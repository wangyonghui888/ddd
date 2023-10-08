package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetailSource;

import java.util.List;

public interface MatchStatisticsInfoDetailSourceService  extends IService<MatchStatisticsInfoDetailSource> {


    int insert(MatchStatisticsInfoDetailSource record);

    int insertOrUpdate(MatchStatisticsInfoDetailSource record);

    int insertOrUpdateSelective(MatchStatisticsInfoDetailSource record);

    int insertSelective(MatchStatisticsInfoDetailSource record);

    int batchInsert(List<MatchStatisticsInfoDetailSource> list);

    int batchInsertOrUpdate(List<MatchStatisticsInfoDetailSource> list, String sourceCode);

    /**
     * 得到小节比分
     * @param matchId
     * @param code
     * @param type
     * @param i
     * @return
     */
    MatchStatisticsInfoDetailSource getSetScoreByParam(Long matchId, String code, String type, int i);

}




