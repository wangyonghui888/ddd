package com.panda.sport.rcs.task.service.profit;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.task.job.operation.ProfitRectangleVo;

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
    void handle(ProfitRectangleVo rectangleVo);
}
