package com.panda.sport.rcs.task.wrapper.statistics;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.statistics
 * @Description :  盘口级别-期望值
 * @Date: 2019-12-11 15:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsProfitMarketService extends IService<RcsProfitMarket> {
    /**
     * @Description   更新
     * @Param [rcsProfitMarket]
     * @Author  toney
     * @Date  17:35 2020/2/6
     * @return java.lang.Integer
     **/
    Integer update(RcsProfitMarket rcsProfitMarket);

    /**
     * 添加或者新增
     * @param rcsProfitMarket
     * @return
     */
    Integer insertOrSave(RcsProfitMarket rcsProfitMarket);
}
