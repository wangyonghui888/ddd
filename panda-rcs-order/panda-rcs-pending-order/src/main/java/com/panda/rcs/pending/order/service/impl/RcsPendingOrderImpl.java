package com.panda.rcs.pending.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.rcs.pending.order.enums.OrderStatusEnum;
import com.panda.rcs.pending.order.mapper.RcsPendingOrderMapper;
import com.panda.rcs.pending.order.pojo.RcsPendingOrder;
import com.panda.rcs.pending.order.service.RcsPendingOrderService;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RcsPendingOrderImpl extends ServiceImpl<RcsPendingOrderMapper, RcsPendingOrder> implements RcsPendingOrderService {

    @Override
    public long selectPendingCountByMatchId(Long matchId, Long userId,Integer matchType) {
        LambdaQueryWrapper<RcsPendingOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RcsPendingOrder::getMatchId, matchId);
        queryWrapper.eq(RcsPendingOrder::getUserId, userId);
        queryWrapper.eq(RcsPendingOrder::getOrderStatus, OrderStatusEnum.PENDING.getCode());
        queryWrapper.eq(RcsPendingOrder::getMatchType,matchType);
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public List<Long> selectPendingMatchIds() {
        LambdaQueryWrapper<RcsPendingOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(RcsPendingOrder::getMatchId);
        queryWrapper.eq(RcsPendingOrder::getOrderStatus, OrderStatusEnum.PENDING.getCode());
        List<RcsPendingOrder> orderList = baseMapper.selectList(queryWrapper);
        return orderList.stream()
                .map(RcsPendingOrder::getMatchId).distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<RcsPendingOrder> selectPendingOrderList(Long matchId, Integer limit) {
        return baseMapper.selectPendingOrderList(matchId, limit);
    }
}
