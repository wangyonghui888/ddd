package com.panda.sport.rcs.wrapper.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.bean.RcsMatchEventTypeInfo;
import com.panda.sport.rcs.mapper.RcsMatchEventTypeInfoMapper;
import com.panda.sport.rcs.pojo.dto.MatchEventInfoDTO;
import com.panda.sport.rcs.wrapper.RcsMatchEventTypeInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RcsMatchEventTypeInfoServiceImpl extends ServiceImpl<RcsMatchEventTypeInfoMapper, RcsMatchEventTypeInfo> implements RcsMatchEventTypeInfoService {

    @Resource
    private RcsMatchEventTypeInfoMapper rcsMatchEventTypeInfoMapper;

    @Override
    public int updateBatch(List<RcsMatchEventTypeInfo> list) {
        return rcsMatchEventTypeInfoMapper.updateBatch(list);
    }


    @Override
    public int batchInsert(List<RcsMatchEventTypeInfo> list) {
        return rcsMatchEventTypeInfoMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(RcsMatchEventTypeInfo record) {
        return rcsMatchEventTypeInfoMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(RcsMatchEventTypeInfo record) {
        return rcsMatchEventTypeInfoMapper.insertOrUpdateSelective(record);
    }

    @Override
    public RcsMatchEventTypeInfo getOneInfo(MatchEventInfoDTO matchEventInfoDTO) {
        LambdaQueryWrapper<RcsMatchEventTypeInfo> lambdaQueryWrapper = new QueryWrapper<RcsMatchEventTypeInfo>().lambda();
        lambdaQueryWrapper.eq(RcsMatchEventTypeInfo::getEventCode, matchEventInfoDTO.getEventCode());
        lambdaQueryWrapper.eq(RcsMatchEventTypeInfo::getSportId, matchEventInfoDTO.getSportId());
        lambdaQueryWrapper.select(RcsMatchEventTypeInfo::getEventName,RcsMatchEventTypeInfo::getEventEnName,RcsMatchEventTypeInfo::getEventType);
        return rcsMatchEventTypeInfoMapper.selectOne(lambdaQueryWrapper);
    }

}


