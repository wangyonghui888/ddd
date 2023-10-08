package com.panda.sport.rcs.mgr.operation.calc;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import com.panda.sport.rcs.pojo.statistics.RcsProfitRectangle;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation.calc
 * @Description :  接口类
 * @Date: 2019-12-17 17:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface IProfitRectangle {
    /**
     * @Description   处理
     * @Param [orderBean]
     * @Author  toney
     * @Date  17:36 2019/12/17
     * @return void
     **/
    void handle(OrderBean orderBean,RcsProfitMarket bean, Integer type);
}
