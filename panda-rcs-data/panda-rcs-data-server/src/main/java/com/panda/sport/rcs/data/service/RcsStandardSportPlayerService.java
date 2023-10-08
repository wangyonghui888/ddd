package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsStandardSportPlayer;

import java.util.List;

public interface RcsStandardSportPlayerService extends IService<RcsStandardSportPlayer> {


    int insertSelective(RcsStandardSportPlayer record);

    int batchInsert(List<RcsStandardSportPlayer> list);

    int insertOrUpdate(RcsStandardSportPlayer record);

    int insertOrUpdateSelective(RcsStandardSportPlayer record);

    int batchInsertOrUpdate(List<RcsStandardSportPlayer> rcsStandardSportPlayers);
}
