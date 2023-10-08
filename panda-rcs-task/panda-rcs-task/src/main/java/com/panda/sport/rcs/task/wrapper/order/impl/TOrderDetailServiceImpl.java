package com.panda.sport.rcs.task.wrapper.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.task.wrapper.order.ITOrderDetailService;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;
import com.panda.sport.rcs.vo.OrderSummaryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 投注单详细信息表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
@Service
@Slf4j
public class TOrderDetailServiceImpl extends ServiceImpl<TOrderDetailMapper, TOrderDetail> implements ITOrderDetailService {
    @Autowired
    private TOrderDetailMapper orderDetailMapper;

    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.TOrderDetail>
     * @Description 按ID升序取topX条数据
     * @Param [id, matchId, limit]
     * @Author toney
     * @Date 11:47 2020/1/27
     **/
    @Override
    public List<TOrderDetail> getTopById(Long id, Long matchId, Integer limit) {
        return orderDetailMapper.getTopById(id, matchId, limit);
    }

    @Override
    public List<OrderDetailStatReportVo> getMarketStatByMatchIdAndPlayId(Long matchId, Long marketCategoryId, Integer matchType) {
        return orderDetailMapper.getMarketStatByMatchIdAndPlayIdAndMatchStatus(matchId, marketCategoryId, matchType);
    }

    @Override
    public List<OrderSummaryVo> getTOrderDetailUpBetTime(Long sportId, Integer matchType, List<Long> oddsId) {
        return orderDetailMapper.getTOrderDetailUpBetTime(sportId, matchType, oddsId);
    }

    @Override
    public List<Long> getOddsId(Long sportId, Long beginTime, Long endTime) {
        return orderDetailMapper.getOddsId(sportId, beginTime, endTime);
    }
}
