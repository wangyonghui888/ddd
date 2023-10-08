package com.panda.rcs.pending.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.rcs.pending.order.pojo.RcsPendingOrder;

import java.util.List;

public interface RcsPendingOrderService extends IService<RcsPendingOrder> {

    long selectPendingCountByMatchId(Long matchId, Long userId,Integer matchType);

    List<Long> selectPendingMatchIds();

    List<RcsPendingOrder> selectPendingOrderList(Long matchId, Integer limit);
}
