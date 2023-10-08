package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.MarketCategorySetMapper;
import com.panda.sport.rcs.data.service.MarketCategorySetService;
import com.panda.sport.rcs.pojo.RcsMarketCategorySet;
import com.panda.sport.rcs.pojo.bo.FindMarketCategoryListAndNamesBO;
import com.panda.sport.rcs.pojo.bo.GetPerformanceSetPlaysBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MarketCategorySetServiceImpl extends ServiceImpl<MarketCategorySetMapper, RcsMarketCategorySet> implements MarketCategorySetService {

    @Autowired
    MarketCategorySetMapper marketCategorySetMapper;

    @Override
    public List<RcsMarketCategorySet> getPerformanceSet(Long sportId) {
        QueryWrapper<RcsMarketCategorySet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RcsMarketCategorySet::getType, 2);
        queryWrapper.lambda().eq(RcsMarketCategorySet::getSportId, sportId);
        return this.list(queryWrapper);
    }

    @Override
    public List<GetPerformanceSetPlaysBO> getPerformanceSetPlays(Long sportId) {
        return  marketCategorySetMapper.getPerformanceSetPlays(sportId);
    }

    @Override
    public List<FindMarketCategoryListAndNamesBO> findMarketCategoryListAndNames(Long sportId, List<Long> setIds) {
        return marketCategorySetMapper.findMarketCategoryListAndNames(sportId,setIds);
    }
}
