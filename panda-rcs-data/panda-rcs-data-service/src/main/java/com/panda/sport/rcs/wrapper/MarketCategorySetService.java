package com.panda.sport.rcs.wrapper;

import com.panda.sport.rcs.pojo.RcsMarketCategorySet;

/**
 * @author Felix
 */
public interface MarketCategorySetService {
    /**
     * @return com.panda.sport.rcs.pojo.RcsMarketCategorySet
     * @Description 根据玩法id查询所在玩法集
     * @Param [idList]
     * @Author Sean
     * @Date 17:46 2019/11/9
     **/
    RcsMarketCategorySet findMarketCategoryListByPlayId(Integer id,Long sportId);

}
