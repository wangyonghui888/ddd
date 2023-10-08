package com.panda.sport.rcs.gts.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsGtsOrderExt;

public interface RcsGtsOrderExtService extends IService<RcsGtsOrderExt> {

    /**
     * 保存GTS订单记录
     *
     * @param
     */
    void addGtsOrder(RcsGtsOrderExt ext);
}
