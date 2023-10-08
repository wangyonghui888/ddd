package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsStandardSportMarketSell;

/**
 * @ClassName RcsStandardSportMarketSellService
 * @Description: TODO
 * @Author Vector
 * @Date 2019/12/30
 **/
public interface RcsStandardSportMarketSellService extends IService<RcsStandardSportMarketSell> {

    /**
     * 根据赛事ID查询
     * @param matchInfoId
     * @return
     */
    RcsStandardSportMarketSell selectStandardMarketSellVo(Long matchInfoId );
}
