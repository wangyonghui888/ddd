package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.RcsStandardSportMarketSellMapper;
import com.panda.sport.rcs.data.service.RcsStandardSportMarketSellService;
import com.panda.sport.rcs.pojo.RcsStandardSportMarketSell;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
/**
 * @ClassName RcsStandardSportMarketSellServiceImpl
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/12/30 
**/
@Service
public class RcsStandardSportMarketSellServiceImpl extends ServiceImpl<RcsStandardSportMarketSellMapper, RcsStandardSportMarketSell> implements RcsStandardSportMarketSellService {

    @Resource
    private RcsStandardSportMarketSellMapper rcsStandardSportMarketSellMapper;

    @Override
    public int updateBatch(List<RcsStandardSportMarketSell> list) {
        return rcsStandardSportMarketSellMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<RcsStandardSportMarketSell> list) {
        return rcsStandardSportMarketSellMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(RcsStandardSportMarketSell record) {
        return rcsStandardSportMarketSellMapper.insertOrUpdate(record);
    }



}
