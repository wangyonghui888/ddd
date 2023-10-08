package com.panda.sport.rcs.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.MarketCategorySetMapper;
import com.panda.sport.rcs.mapper.RcsCodeMapper;
import com.panda.sport.rcs.pojo.RcsMarketCategorySet;
import com.panda.sport.rcs.wrapper.MarketCategorySetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Felix
 */
@Service
public class MarketCategorySetServiceImpl extends ServiceImpl<MarketCategorySetMapper, RcsMarketCategorySet> implements MarketCategorySetService {
    @Autowired
    MarketCategorySetMapper marketCategorySetMapper;

    @Autowired
    private RcsCodeMapper rcsCodeMapper;

    @Override
    public RcsMarketCategorySet findMarketCategoryListByPlayId(Integer id,Long sportId) {
        return marketCategorySetMapper.findMarketCategoryListByPlayId(id,sportId);
    }

}
