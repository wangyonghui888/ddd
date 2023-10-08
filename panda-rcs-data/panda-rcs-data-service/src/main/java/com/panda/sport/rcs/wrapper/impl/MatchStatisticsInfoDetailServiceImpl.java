package com.panda.sport.rcs.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.statistics.MatchStatisticsInfoDetailMapper;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.wrapper.MatchStatisticsInfoDetailService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MatchStatisticsInfoDetailServiceImpl extends ServiceImpl<MatchStatisticsInfoDetailMapper, MatchStatisticsInfoDetail> implements MatchStatisticsInfoDetailService {

    @Resource
    private MatchStatisticsInfoDetailMapper matchStatisticsInfoDetailMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return matchStatisticsInfoDetailMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insertOrUpdate(MatchStatisticsInfoDetail record) {
        return matchStatisticsInfoDetailMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(MatchStatisticsInfoDetail record) {
        return matchStatisticsInfoDetailMapper.insertOrUpdateSelective(record);
    }

    @Override
    public int insertSelective(MatchStatisticsInfoDetail record) {
        return matchStatisticsInfoDetailMapper.insertSelective(record);
    }

    @Override
    public MatchStatisticsInfoDetail selectByPrimaryKey(Long id) {
        return matchStatisticsInfoDetailMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(MatchStatisticsInfoDetail record) {
        return matchStatisticsInfoDetailMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(MatchStatisticsInfoDetail record) {
        return matchStatisticsInfoDetailMapper.updateByPrimaryKey(record);
    }

    @Override
    public int batchInsert(List<MatchStatisticsInfoDetail> list) {
        return matchStatisticsInfoDetailMapper.batchInsert(list);
    }

    @Override
    public int batchInsertOrUpdate(List<MatchStatisticsInfoDetail> matchStatisticsInfoDetailList) {
        if(CollectionUtils.isEmpty(matchStatisticsInfoDetailList)) return 0;
        return matchStatisticsInfoDetailMapper.batchInsertOrUpdate(matchStatisticsInfoDetailList);
    }

    @Override
    public List<MatchStatisticsInfoDetail> queryStatisticsInfoDetailsByMatchId(Long standardMatchId) {
       return matchStatisticsInfoDetailMapper.queryStatisticsInfoDetailsByMatchId(standardMatchId);
    }

    @Override
    public String queryMatchScore(Long matchId) {
        return matchStatisticsInfoDetailMapper.queryMatchScore(matchId);
    }
}

