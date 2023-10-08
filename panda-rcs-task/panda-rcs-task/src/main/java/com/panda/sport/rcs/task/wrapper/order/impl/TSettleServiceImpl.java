package com.panda.sport.rcs.task.wrapper.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.TSettleMapper;
import com.panda.sport.rcs.pojo.TSettle;
import com.panda.sport.rcs.pojo.report.CalcSettleItem;
import com.panda.sport.rcs.task.wrapper.order.ITSettleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 结算表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-12-24
 */
@Service
public class TSettleServiceImpl extends ServiceImpl<TSettleMapper, TSettle> implements ITSettleService {

    @Autowired
    TSettleMapper settleMapper;

    @Override
    public List<CalcSettleItem> getCustomizedOrderList(Long beginTime, Long endTime,Integer start,Integer size) {
        List<CalcSettleItem> pageResult = settleMapper.getCustomizedOrderList(beginTime,endTime,start,size);
        return pageResult;
    }

    @Override
    public Long getCountCustomizedOrder(Long beginTime, Long endTime) {
        return settleMapper.getCountCustomizedOrder(beginTime,endTime);
    }

    @Override
    public List<TSettle> selectByMatchId(Long matchId){
        return settleMapper.selectByMatchId(matchId);
    }
}
