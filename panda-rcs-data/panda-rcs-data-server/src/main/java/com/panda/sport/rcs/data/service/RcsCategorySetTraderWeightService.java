package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsCategorySetTraderWeight;

import java.util.List;

public interface RcsCategorySetTraderWeightService extends IService<RcsCategorySetTraderWeight> {

    int insertOrUpdate(RcsCategorySetTraderWeight record);

    int batchInsertOrUpdate(List<RcsCategorySetTraderWeight> list);

}
