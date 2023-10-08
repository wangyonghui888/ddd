package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsStandardSportPlayer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RcsStandardSportPlayerMapper  extends BaseMapper<RcsStandardSportPlayer> {
    int insertSelective(RcsStandardSportPlayer record);

    int batchInsert(@Param("list") List<RcsStandardSportPlayer> list);

    int insertOrUpdate(RcsStandardSportPlayer record);

    int insertOrUpdateSelective(RcsStandardSportPlayer record);

    int batchInsertOrUpdate(List<RcsStandardSportPlayer> rcsStandardSportPlayers);
}