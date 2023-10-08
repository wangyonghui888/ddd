package com.panda.sport.rcs.oddin.service.handler;

import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderTy;

/**
 * @author Z9-conway
 */
public interface RcsOddinOrderTyHandler {
    /**
     * 插入RCS_GTS_ORDER_EXT表数据
     * @param ext
     */
    void save(RcsOddinOrderTy ext);

    void update(RcsOddinOrderTy ext);

    RcsOddinOrderTy selectOne(String orderNo);
}
