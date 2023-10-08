package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.data.mapper.MatchStatisticsInfoDetailSourceMapper;
import com.panda.sport.rcs.data.service.MatchStatisticsInfoDetailSourceService;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetailSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MatchStatisticsInfoDetailSourceServiceImpl extends ServiceImpl<MatchStatisticsInfoDetailSourceMapper, MatchStatisticsInfoDetailSource> implements MatchStatisticsInfoDetailSourceService {

    @Resource
    private MatchStatisticsInfoDetailSourceMapper matchStatisticsInfoDetailSourceMapper;

    @Override
    public int insert(MatchStatisticsInfoDetailSource record) {
        return matchStatisticsInfoDetailSourceMapper.insert(record);
    }

    @Override
    public int insertOrUpdate(MatchStatisticsInfoDetailSource record) {
        return matchStatisticsInfoDetailSourceMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(MatchStatisticsInfoDetailSource record) {
        return matchStatisticsInfoDetailSourceMapper.insertOrUpdateSelective(record);
    }

    @Override
    public int insertSelective(MatchStatisticsInfoDetailSource record) {
        return matchStatisticsInfoDetailSourceMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<MatchStatisticsInfoDetailSource> list) {
        return matchStatisticsInfoDetailSourceMapper.batchInsert(list);
    }

    @Override
    public int batchInsertOrUpdate(List<MatchStatisticsInfoDetailSource> list, String sourceCode) {
        if(CollectionUtils.isEmpty(list)){return 0;}
        List<MatchStatisticsInfoDetailSource> matchStatisticsInfoDetailSources = BeanCopyUtils.copyPropertiesList(list, MatchStatisticsInfoDetailSource.class);
        for (MatchStatisticsInfoDetailSource matchStatisticsInfoDetailSource : matchStatisticsInfoDetailSources) {
            matchStatisticsInfoDetailSource.setDataSourceCode(sourceCode);
        }
        return matchStatisticsInfoDetailSourceMapper.batchInsertOrUpdate(matchStatisticsInfoDetailSources);
    }

    @Override
    public MatchStatisticsInfoDetailSource getSetScoreByParam(Long matchId, String code, String type, int i) {
        QueryWrapper<MatchStatisticsInfoDetailSource> matchStatisticsInfoDetailSourceQueryWrapper = new QueryWrapper<>();
        matchStatisticsInfoDetailSourceQueryWrapper.lambda().eq(MatchStatisticsInfoDetailSource::getStandardMatchId, matchId);
        matchStatisticsInfoDetailSourceQueryWrapper.lambda().eq(MatchStatisticsInfoDetailSource::getDataSourceCode,type);
        matchStatisticsInfoDetailSourceQueryWrapper.lambda().eq(MatchStatisticsInfoDetailSource::getCode,code);
        matchStatisticsInfoDetailSourceQueryWrapper.lambda().eq(MatchStatisticsInfoDetailSource::getFirstNum,i);
        matchStatisticsInfoDetailSourceQueryWrapper.lambda().eq(MatchStatisticsInfoDetailSource::getSecondNum,0);
        matchStatisticsInfoDetailSourceQueryWrapper.last("limit 1");
        return matchStatisticsInfoDetailSourceMapper.selectOne(matchStatisticsInfoDetailSourceQueryWrapper);
    }

}




