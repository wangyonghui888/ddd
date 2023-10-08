package com.panda.sport.rcs.data.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.data.service.RcsLanguageInternationService;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
@Service
public class RcsLanguageInternationServiceImpl extends ServiceImpl<RcsLanguageInternationMapper, RcsLanguageInternation> implements RcsLanguageInternationService  {


    @Resource
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;

    @Override
    public int updateBatch(List<RcsLanguageInternation> list) {
        return rcsLanguageInternationMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<RcsLanguageInternation> list) {
        if(CollectionUtils.isEmpty(list)){return 0;}
        return rcsLanguageInternationMapper.batchInsert(list);
    }

    @Override
    public Integer batchInsertOrUpdate(List<RcsLanguageInternation> list) {
        if(CollectionUtils.isEmpty(list)){return 0;}
        return rcsLanguageInternationMapper.batchInsertOrUpdate(list);
    }

    @Override
    public int insertOrUpdate(RcsLanguageInternation record) {
        return rcsLanguageInternationMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(RcsLanguageInternation record) {
        return rcsLanguageInternationMapper.insertOrUpdateSelective(record);
    }

    @Override
    public int batchInsertOrUpdateMerge(List<RcsLanguageInternation> list) {
        if(CollectionUtils.isEmpty(list)){return 0;}
        return rcsLanguageInternationMapper.batchInsertOrUpdateMerge(list);
    }

    @Override
    public int insertOrUpdateMerge(RcsLanguageInternation record) {
        if(null==record){return 0;}
        return rcsLanguageInternationMapper.insertOrUpdateMerge(record);
    }

}
