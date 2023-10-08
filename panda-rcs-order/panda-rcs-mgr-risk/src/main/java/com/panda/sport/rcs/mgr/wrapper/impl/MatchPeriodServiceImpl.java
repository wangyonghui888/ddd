package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.MatchPeriodMapper;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.mgr.wrapper.MatchPeriodService;
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
    public MatchPeriod getOne(Long macthId, Integer period) {
        QueryWrapper<MatchPeriod> wrapper = new QueryWrapper();
        wrapper.lambda().eq(MatchPeriod::getStandardMatchId,macthId);
        wrapper.lambda().eq(MatchPeriod::getPeriod,period);
        return matchPeriodMapper.selectOne(wrapper);
    }

}
