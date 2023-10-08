package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.MatchPeriod;

import java.util.List;

/**
 * @ClassName MatchPeriodService
 * @Description: TODO
 * @Author Vector
 * @Date 2019/11/19
 **/
public interface MatchPeriodService extends IService<MatchPeriod> {

    int batchInsert(List<MatchPeriod> list);

    int insertOrUpdate(MatchPeriod record);

    /**
     * 查询阶段比分
     * @param macthId
     * @param period
     * @return
     */
    MatchPeriod getOne(Long macthId, Integer period);

    /**
     * 获取赛事最后的比分信息
     * @param matchId
     * @return
     */
    MatchPeriod getLast(Long matchId);

}
