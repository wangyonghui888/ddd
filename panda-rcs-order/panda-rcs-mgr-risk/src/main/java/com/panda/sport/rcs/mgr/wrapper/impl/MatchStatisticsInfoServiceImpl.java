package com.panda.sport.rcs.mgr.wrapper.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.MatchStatisticsInfoMapper;
import com.panda.sport.rcs.mgr.wrapper.MatchStatisticsInfoService;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;

/**
 * @ClassName MatchStatisticsInfoServiceImpl
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/11
 **/
@Service
public class MatchStatisticsInfoServiceImpl extends ServiceImpl<MatchStatisticsInfoMapper, MatchStatisticsInfo> implements MatchStatisticsInfoService {

    @Autowired
    MatchStatisticsInfoMapper matchStatisticsInfoMapper;

    @Override
    public MatchStatisticsInfo getMatchInfoByMatchId(Long matchId) {
        MatchStatisticsInfo matchStatisticsInfo = matchStatisticsInfoMapper.getMatchInfoByMatchId(matchId);
        if (matchStatisticsInfo == null) {
            matchStatisticsInfo = new MatchStatisticsInfo();
        }
        return matchStatisticsInfo;
    }
}
