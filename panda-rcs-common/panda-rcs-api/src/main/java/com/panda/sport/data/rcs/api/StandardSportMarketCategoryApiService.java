package com.panda.sport.data.rcs.api;


public interface StandardSportMarketCategoryApiService {

    /**
     * 标准玩法同步接口（含投注项）
     * @param requestParam
     * @return
     */
    Response putSportMarketCategory(Request<Long> requestParam);
}
