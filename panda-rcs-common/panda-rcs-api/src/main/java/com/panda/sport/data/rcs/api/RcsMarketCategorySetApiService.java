package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.MarketCategoryCetBean;
import com.panda.sport.rcs.log.annotion.NotWriteLog;

/**
 * @author :  Felix
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.data.rcs.api
 * @Description :  TODO
 * @Date: 2019-10-03 13:16
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsMarketCategorySetApiService {
    /**
     * 玩法集
     *
     * @return
     */
	Response putSportMarketCategorySet(Request<MarketCategoryCetBean> requestParam);

    /**
     * 玩法
     *
     * @param requestParam
     * @return
     */
    Response putSportMarketCategorySetPlay(Request<Long> requestParam);

    /**
     * 根据玩法ID 查询margin
     * @param requestParam
     * @return
     */
    @NotWriteLog
    Response getSportMarketCategorySetMargin (Request<MarketCategoryCetBean> requestParam);


    /**
     * 根据体育种类ID，获取展示型玩法id
     *
     */
    Response putSportMarketCategoryBySportId(Request<Long> requestParam);
}
