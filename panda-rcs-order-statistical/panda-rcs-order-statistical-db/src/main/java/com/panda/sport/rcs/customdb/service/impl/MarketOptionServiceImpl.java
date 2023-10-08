package com.panda.sport.rcs.customdb.service.impl;

import com.panda.sport.rcs.customdb.entity.MarketEntity;
import com.panda.sport.rcs.customdb.entity.MarketOptionEntity;
import com.panda.sport.rcs.customdb.mapper.MarketOptionMapper;
import com.panda.sport.rcs.customdb.service.IMarketOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service.impl
 * @description :  TODO
 * @date: 2020-07-19 14:19
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service("marketOptionServiceImpl")
public class MarketOptionServiceImpl implements IMarketOptionService {

    @Autowired
    MarketOptionMapper marketOptionMapper;

    @Override
    public List<MarketOptionEntity> getMarketOptionByIds(Set<Long> ids) {
        return marketOptionMapper.getMarketOptionByIds(ids);
    }


    @Override
    public List<MarketEntity> getMarketByIds(Set<Long> ids) {
        return marketOptionMapper.getMarketByIds(ids);
    }
}
