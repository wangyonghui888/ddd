package com.panda.sport.rcs.task.wrapper;

import com.panda.sport.rcs.pojo.report.CalcSettleItem;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.task.wrapper
 * @Description :  TODO
 * @Date: 2019-12-26 20:24
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface OrderStatisticTimeService {
    /**
     * @return void
     * @Description //结算报表处理
     * @Param [calcSettleItemList]
     * @Author kimi
     * @Date 2019/12/26
     **/
    Boolean orderStatisticTimeDealwith(CalcSettleItem calcSettleItem);

    void initDataByDate(Long startDate,Long endDate);


}
