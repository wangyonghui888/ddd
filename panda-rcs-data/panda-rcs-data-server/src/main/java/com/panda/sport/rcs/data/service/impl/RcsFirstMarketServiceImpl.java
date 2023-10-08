package com.panda.sport.rcs.data.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.RcsFirstMarketMapper;
import com.panda.sport.rcs.data.service.RcsFirstMarketService;
import com.panda.sport.rcs.pojo.RcsFirstMarket;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RcsFirstMarketServiceImpl  extends ServiceImpl<RcsFirstMarketMapper, RcsFirstMarket> implements RcsFirstMarketService {

    @Resource
    private RcsFirstMarketMapper rcsFirstMarketMapper;

    @Override
    public int updateBatch(List<RcsFirstMarket> list) {
        return rcsFirstMarketMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<RcsFirstMarket> list) {
        if(CollectionUtils.isEmpty(list)){return 0;}
        return rcsFirstMarketMapper.batchInsert(list);
    }

    @Override
    public int batchInsertOrUpdate(List<RcsFirstMarket> list) {
        if(CollectionUtils.isEmpty(list)){return 0;}
        return rcsFirstMarketMapper.batchInsertOrUpdate(list);
    }

    @Override
    public int insertOrUpdate(RcsFirstMarket record) {
        return rcsFirstMarketMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(RcsFirstMarket record) {
        return rcsFirstMarketMapper.insertOrUpdateSelective(record);
    }

    @Override
    public List selectData(Long standardMatchInfoId, Long marketCategoryId) {
        QueryWrapper<RcsFirstMarket> rcsFirstMarketQueryWrapper = new QueryWrapper<>();
        rcsFirstMarketQueryWrapper.lambda().eq(RcsFirstMarket::getStandardMatchId,standardMatchInfoId);
        rcsFirstMarketQueryWrapper.lambda().eq(RcsFirstMarket::getPlayId,marketCategoryId);
        rcsFirstMarketQueryWrapper.lambda().eq(RcsFirstMarket::getType,1);
        List<Object> objects = rcsFirstMarketMapper.selectObjs(rcsFirstMarketQueryWrapper);
        return objects;
    }

}


