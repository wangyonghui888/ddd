package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMarketChampionExt;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RcsMarketChampionExtMapper extends BaseMapper<RcsMarketChampionExt> {

    int batchInsert(@Param("list") List<RcsMarketChampionExt> list);

    int insertOrUpdate(RcsMarketChampionExt record);

    int insertOrUpdateSelective(RcsMarketChampionExt record);

    int batchInsertOrUpdate(List<RcsMarketChampionExt> list);
}