package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsStandardSportMarketSellMapper;
import com.panda.sport.rcs.pojo.RcsStandardSportMarketSell;
import com.panda.sport.rcs.task.wrapper.RcsStandardSportMarketSellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName RcsStandardSportMarketSellServiceImpl
 * @Description: TODO
 * @Author Vector
 * @Date 2019/12/30
 **/
@Service
public class RcsStandardSportMarketSellServiceImpl extends ServiceImpl<RcsStandardSportMarketSellMapper, RcsStandardSportMarketSell> implements RcsStandardSportMarketSellService {

    @Autowired
    private RcsStandardSportMarketSellMapper rcsStandardSportMarketSellMapper;


    @Override
    public RcsStandardSportMarketSell selectStandardMarketSellVo(Long matchInfoId) {
        QueryWrapper<RcsStandardSportMarketSell> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RcsStandardSportMarketSell::getMatchInfoId,matchInfoId);
        return rcsStandardSportMarketSellMapper.selectOne(wrapper);
    }
}
