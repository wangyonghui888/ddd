package com.panda.sport.rcs.data.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsFirstMarket;

import java.util.List;

public interface RcsFirstMarketService  extends IService<RcsFirstMarket> {


    int updateBatch(List<RcsFirstMarket> list);

    int batchInsert(List<RcsFirstMarket> list);

    int batchInsertOrUpdate(List<RcsFirstMarket> list);

    int insertOrUpdate(RcsFirstMarket record);

    int insertOrUpdateSelective(RcsFirstMarket record);

    List selectData(Long standardMatchInfoId, Long marketCategoryId);
}


