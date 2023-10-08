package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.MatchPeriodMapper;
import com.panda.sport.rcs.data.service.MatchPeriodService;
import com.panda.sport.rcs.pojo.MatchPeriod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName MatchPeriodServiceImpl
 * @Description: TODO
 * @Author Vector
 * @Date 2019/11/19
 **/
@Service
public class MatchPeriodServiceImpl extends ServiceImpl<MatchPeriodMapper, MatchPeriod> implements MatchPeriodService {

    @Resource
    private MatchPeriodMapper matchPeriodMapper;

    @Override
    public int batchInsert(List<MatchPeriod> list) {
        return matchPeriodMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(MatchPeriod record) {
        if (StringUtils.isBlank(record.getScore()) || record.getScore().contains("null")) return 0;
        return matchPeriodMapper.insertOrUpdate(record);
    }

    @Override
    public MatchPeriod getOne(Long macthId, Integer period) {
        QueryWrapper<MatchPeriod> wrapper = new QueryWrapper();
        wrapper.lambda().eq(MatchPeriod::getStandardMatchId, macthId);
        wrapper.lambda().eq(MatchPeriod::getPeriod, period);
        return matchPeriodMapper.selectOne(wrapper);
    }

    @Override
    public MatchPeriod getLast(Long matchId) {

        return matchPeriodMapper.getLast(matchId);
    }


}
