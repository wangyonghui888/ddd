package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.RcsStandardSportPlayerMapper;
import com.panda.sport.rcs.data.service.RcsStandardSportPlayerService;
import com.panda.sport.rcs.pojo.RcsStandardSportPlayer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RcsStandardSportPlayerServiceImpl extends ServiceImpl<RcsStandardSportPlayerMapper, RcsStandardSportPlayer> implements RcsStandardSportPlayerService {

    @Resource
    private RcsStandardSportPlayerMapper rcsStandardSportPlayerMapper;

    @Override
    public int insertSelective(RcsStandardSportPlayer record) {
        return rcsStandardSportPlayerMapper.insertSelective(record);
    }

    @Override
    public int batchInsert(List<RcsStandardSportPlayer> list) {
        return rcsStandardSportPlayerMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(RcsStandardSportPlayer record) {
        return rcsStandardSportPlayerMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(RcsStandardSportPlayer record) {
        return rcsStandardSportPlayerMapper.insertOrUpdateSelective(record);
    }

    @Override
    public int batchInsertOrUpdate(List<RcsStandardSportPlayer> rcsStandardSportPlayers) {
        if(CollectionUtils.isEmpty(rcsStandardSportPlayers)){return 0;}
        rcsStandardSportPlayerMapper.batchInsertOrUpdate(rcsStandardSportPlayers);

        return 0;
    }

}
