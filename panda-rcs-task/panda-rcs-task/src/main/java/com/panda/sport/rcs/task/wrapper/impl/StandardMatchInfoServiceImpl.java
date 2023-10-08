package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.cache.RcsCacheContant;
import com.panda.sport.rcs.task.wrapper.StandardMatchInfoService;
import com.panda.sport.rcs.vo.MarketLiveOddsQueryVo;
import com.panda.sport.rcs.vo.StandardMatchInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Service
@Slf4j
public class StandardMatchInfoServiceImpl extends ServiceImpl<StandardMatchInfoMapper, StandardMatchInfo> implements StandardMatchInfoService {

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;

    @Override
    public List<StandardMatchInfoVo> queryMatchesNoLimitByV2(MarketLiveOddsQueryVo marketLiveOddsQueryVo) {
        List<StandardMatchInfoVo> list = standardMatchInfoMapper.selectPageByConditionV2(marketLiveOddsQueryVo);
        return list;
    }

    @Override
    public List<StandardMatchInfo> selectMatchs(MarketLiveOddsQueryVo marketLiveOddsQueryVo) {
        return standardMatchInfoMapper.selectMatchs(marketLiveOddsQueryVo);
    }

    @Override
    public StandardMatchInfo selectOne(Long matchId) {
    	return RcsCacheContant.MATCHINFO_CACHE.get(matchId, id -> standardMatchInfoMapper.selectById(matchId));
    	
//        Object o = GuavaCache.get(String.format("standardMatchInfo_%s", matchId));
//        StandardMatchInfo matchInfo = new StandardMatchInfo();
//        if (null != o) {
//            matchInfo = JsonFormatUtils.fromJson(JsonFormatUtils.toJson(o), StandardMatchInfo.class);
//        } else {
//            matchInfo = standardMatchInfoMapper.selectById(matchId);
//            GuavaCache.put(String.format("standardMatchInfo_%s", matchId),JsonFormatUtils.toJson(matchInfo));
//        }
//        return matchInfo;
    }

    @Override
    public List<StandardMatchInfo> getCurrentBillDayMatchInfo(Collection<Long> sportIds) {
        long endTime = DateUtils.getTomorrowMidDay();
        long startTime = endTime - 24 * 60 * 60 * 1000;
        LambdaQueryWrapper<StandardMatchInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.ge(StandardMatchInfo::getBeginTime, startTime)
                .lt(StandardMatchInfo::getBeginTime, endTime);
        if (CollectionUtils.isNotEmpty(sportIds)) {
            wrapper.in(StandardMatchInfo::getSportId, sportIds);
        }
        wrapper.select(StandardMatchInfo::getId, StandardMatchInfo::getSportId);
        return this.list(wrapper);
    }
}
