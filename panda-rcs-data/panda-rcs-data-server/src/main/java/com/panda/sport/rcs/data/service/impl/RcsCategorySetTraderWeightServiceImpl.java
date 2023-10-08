package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.RcsCategorySetTraderWeightMapper;
import com.panda.sport.rcs.data.service.RcsCategorySetTraderWeightService;
import com.panda.sport.rcs.pojo.RcsCategorySetTraderWeight;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RcsCategorySetTraderWeightServiceImpl extends ServiceImpl<RcsCategorySetTraderWeightMapper, RcsCategorySetTraderWeight> implements RcsCategorySetTraderWeightService {

    @Resource
    private RcsCategorySetTraderWeightMapper rcsCategorySetTraderWeightMapper;


    @Override
    public int insertOrUpdate(RcsCategorySetTraderWeight record) {
        return rcsCategorySetTraderWeightMapper.insertOrUpdate(record);
    }

    @Override
    public int batchInsertOrUpdate(List<RcsCategorySetTraderWeight> list) {
        if(CollectionUtils.isEmpty(list)){return 0;}
        return rcsCategorySetTraderWeightMapper.batchInsertOrUpdate(list);
    }



}
