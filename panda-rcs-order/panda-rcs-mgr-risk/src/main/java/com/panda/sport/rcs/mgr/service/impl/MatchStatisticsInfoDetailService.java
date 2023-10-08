package com.panda.sport.rcs.mgr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.panda.sport.rcs.mapper.statistics.MatchStatisticsInfoDetailMapper;
import com.panda.sport.rcs.mgr.utils.ScoreTypeEnum;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MatchStatisticsInfoDetailService{

    @Autowired
    MatchStatisticsInfoDetailMapper matchStatisticsInfoDetailMapper;

    public MatchStatisticsInfoDetail getByScoreType(Long matchId, ScoreTypeEnum scoreTypeEnum) {
        LambdaQueryWrapper<MatchStatisticsInfoDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MatchStatisticsInfoDetail::getStandardMatchId, matchId)
                .eq(MatchStatisticsInfoDetail::getCode, scoreTypeEnum.getCode())
                .eq(MatchStatisticsInfoDetail::getFirstNum, scoreTypeEnum.getFirstNum())
                .eq(MatchStatisticsInfoDetail::getSecondNum, scoreTypeEnum.getSecondNum())
                .last("LIMIT 1");
        MatchStatisticsInfoDetail result = matchStatisticsInfoDetailMapper.selectOne(wrapper);
        if (result == null) {
            result = new MatchStatisticsInfoDetail();
            result.setStandardMatchId(matchId);
            result.setCode(scoreTypeEnum.getCode());
            result.setFirstNum(scoreTypeEnum.getFirstNum());
            result.setSecondNum(scoreTypeEnum.getSecondNum());
            result.setT1(0);
            result.setT2(0);
        }
        return result;
    }
}

