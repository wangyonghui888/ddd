package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsStandardSportMarketSell;

import java.util.List;
    /**
 * @ClassName RcsStandardSportMarketSellService
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/12/30 
**/
public interface RcsStandardSportMarketSellService  extends IService<RcsStandardSportMarketSell> {


    int updateBatch(List<RcsStandardSportMarketSell> list);

    int batchInsert(List<RcsStandardSportMarketSell> list);

    int insertOrUpdate(RcsStandardSportMarketSell record);


}
