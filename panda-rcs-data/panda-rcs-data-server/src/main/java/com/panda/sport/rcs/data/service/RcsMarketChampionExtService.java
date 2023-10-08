package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsMarketChampionExt;

import java.util.List;

public interface RcsMarketChampionExtService extends IService<RcsMarketChampionExt> {

    int batchInsert(List<RcsMarketChampionExt> list);

    int insertOrUpdate(RcsMarketChampionExt record);

    int insertOrUpdateSelective(RcsMarketChampionExt record);

    int batchInsertOrUpdate(List<RcsMarketChampionExt> rcsMarketChampionExts);
}
