package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsStandardSportMarketSellMapper;
import com.panda.sport.rcs.pojo.RcsStandardSportMarketSell;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @ClassName RcsStandardSportMarketSellServiceImpl
 * @Description: TODO
 * @Author carver
 * @Date 2019/12/30
 **/
@Service
public class RcsStandardSportMarketSellServiceImpl extends ServiceImpl<RcsStandardSportMarketSellMapper, RcsStandardSportMarketSell>{

    @Resource
    private RcsStandardSportMarketSellMapper rcsStandardSportMarketSellMapper;

    public RcsStandardSportMarketSell queryMarketSell(RcsStandardSportMarketSell record){
        return rcsStandardSportMarketSellMapper.queryMarketSell(record);
    }

}
