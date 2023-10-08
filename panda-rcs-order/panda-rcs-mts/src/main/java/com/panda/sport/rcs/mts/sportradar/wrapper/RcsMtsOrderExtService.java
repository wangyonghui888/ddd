package com.panda.sport.rcs.mts.sportradar.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsMtsOrderExt;

public interface RcsMtsOrderExtService extends IService<RcsMtsOrderExt> {

    /**
     * 保存MTS订单记录
     *
     * @param rcsMtsOrderExt
     */
    void addMtsOrder(RcsMtsOrderExt rcsMtsOrderExt);
}
