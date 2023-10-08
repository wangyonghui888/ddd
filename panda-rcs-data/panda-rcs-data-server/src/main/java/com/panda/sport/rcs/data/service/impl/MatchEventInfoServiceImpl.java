package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.MatchEventInfoMapper;
import com.panda.sport.rcs.data.service.MatchEventInfoService;
import com.panda.sport.rcs.pojo.MatchEventInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName MatchEventInfoServiceImpl
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/10
 **/
@Service
public class MatchEventInfoServiceImpl extends ServiceImpl<MatchEventInfoMapper, MatchEventInfo> implements MatchEventInfoService {

    @Autowired
    MatchEventInfoMapper matchEventInfoMapper;

    @Override
    public int insertOrUpdate(MatchEventInfo record) {
        return matchEventInfoMapper.insertOrUpdate(record);
    }

    @Override
    public MatchEventInfo getLast(Long standardMatchId) {
        LambdaQueryWrapper<MatchEventInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MatchEventInfo::getStandardMatchId, standardMatchId);
        wrapper.orderByDesc(MatchEventInfo::getCreateTime);
        wrapper.last(" limit 1");
        return getOne(wrapper, false);
    }
}
