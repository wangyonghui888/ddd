package com.panda.sport.rcs.oddin.service.handler;

import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderDj;

/**
 * @author Z9-conway
 */
public interface RcsOddinOrderDjHandler {
    /**
     * 插入RCS_GTS_ORDER_EXT表数据
     * @param ext
     */
    void save(RcsOddinOrderDj ext);

    void update(RcsOddinOrderDj ext);
}
