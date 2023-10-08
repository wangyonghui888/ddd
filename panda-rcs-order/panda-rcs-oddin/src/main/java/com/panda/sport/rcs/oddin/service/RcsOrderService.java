package com.panda.sport.rcs.oddin.service;

import com.panda.sport.data.rcs.vo.oddin.TicketVo;

/**
 * 处理风控订单业务
 * @author Z9-conway
 */
public interface RcsOrderService {
    /**
     * 处理体育业务注单oddin接拒
     * @param vo
     */
    void rejectOrder(TicketVo vo);
}
