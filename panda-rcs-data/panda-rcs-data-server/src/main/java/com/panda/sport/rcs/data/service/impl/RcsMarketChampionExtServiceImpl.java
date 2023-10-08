package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.RcsMarketChampionExtMapper;
import com.panda.sport.rcs.data.service.RcsMarketChampionExtService;
import com.panda.sport.rcs.pojo.RcsMarketChampionExt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RcsMarketChampionExtServiceImpl extends ServiceImpl<RcsMarketChampionExtMapper, RcsMarketChampionExt> implements RcsMarketChampionExtService {

    @Resource
    private RcsMarketChampionExtMapper rcsMarketChampionExtMapper;



    @Override
    public int batchInsert(List<RcsMarketChampionExt> list) {
        return rcsMarketChampionExtMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(RcsMarketChampionExt record) {
        return rcsMarketChampionExtMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(RcsMarketChampionExt record) {
        return rcsMarketChampionExtMapper.insertOrUpdateSelective(record);
    }

    @Override
    public int batchInsertOrUpdate(List<RcsMarketChampionExt> list) {
        if(CollectionUtils.isEmpty(list)){return 0;}
        return rcsMarketChampionExtMapper.batchInsertOrUpdate(list);
    }

}
