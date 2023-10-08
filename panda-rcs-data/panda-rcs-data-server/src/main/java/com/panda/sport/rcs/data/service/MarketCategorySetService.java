package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsMarketCategorySet;
import com.panda.sport.rcs.pojo.bo.FindMarketCategoryListAndNamesBO;
import com.panda.sport.rcs.pojo.bo.GetPerformanceSetPlaysBO;

import java.util.List;


public interface MarketCategorySetService extends IService<RcsMarketCategorySet> {


    List<RcsMarketCategorySet> getPerformanceSet(Long sportId);

    List<GetPerformanceSetPlaysBO> getPerformanceSetPlays(Long sportId);

    /**
     * 玩法集下的玩法列表，用于玩法集列表下的二级目录,并得到国际化
     * @param setIds
     * @return
     */
    List<FindMarketCategoryListAndNamesBO> findMarketCategoryListAndNames(Long sportId, List<Long> setIds);
}
