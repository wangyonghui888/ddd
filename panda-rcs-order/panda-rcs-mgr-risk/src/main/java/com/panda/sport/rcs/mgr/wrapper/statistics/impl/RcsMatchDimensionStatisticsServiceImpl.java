package com.panda.sport.rcs.mgr.wrapper.statistics.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.statistics.RcsMatchDimensionStatisticsMapper;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsTotalValueNearlyOneTimeService;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.mgr.wrapper.MarketViewService;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsMatchDimensionStatisticsService;
import com.panda.sport.rcs.pojo.statistics.RcsTotalValueNearlyOneTime;
import com.panda.sport.rcs.vo.statistics.RcsMatchDimensionStatisticsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.statistics.impl
 * @Description :  更新单场比赛
 * @Date: 2019-11-05 15:59
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsMatchDimensionStatisticsServiceImpl extends ServiceImpl<RcsMatchDimensionStatisticsMapper, RcsMatchDimensionStatistics> implements RcsMatchDimensionStatisticsService {
    @Autowired
    private RcsMatchDimensionStatisticsMapper mapper;

    @Autowired
    private MarketViewService marketViewService;
    @Autowired
    private RcsTotalValueNearlyOneTimeService rcsTotalValueNearlyOneTimeService;


    /**
     * 订单mq处理
     * 判断是否存在
     * @param rcsMatchDimensionStatistics
     * @return true为存在；false为不存在
     */
    private Boolean exists(RcsMatchDimensionStatistics rcsMatchDimensionStatistics){
        RcsMatchDimensionStatistics m = mapper.selectById(rcsMatchDimensionStatistics.getMatchId());
        if(m == null){
            return false;
        }
        return true;
    }

    /**
     * 查询近一小时内实货量
     * @param matchId
     * @return
     */
    @Override
    public List<RcsMatchDimensionStatisticsVo> searchNearlyOneHourRealTimeValue(Long[] matchId){
        List<Long> longs = Arrays.asList(matchId);
        List<RcsMatchDimensionStatisticsVo> list = new ArrayList<>();
        for(int i=0;i<longs.size();i++){
            if(longs.get(i)!=null) {
                RcsTotalValueNearlyOneTime rcsTotalValueNearlyOneTime= rcsTotalValueNearlyOneTimeService.getById(longs.get(i));
                RcsMatchDimensionStatisticsVo statisticsVo= null;
                if(rcsTotalValueNearlyOneTime!=null) {
                    statisticsVo = mapper.searchNearlyOneHourRealTimeValue(longs.get(i), rcsTotalValueNearlyOneTime.getOrderDetailId());
                }else{
                    statisticsVo = new RcsMatchDimensionStatisticsVo();
                    statisticsVo.setRealTimeValue(BigDecimal.ZERO);
                    statisticsVo.setMatchId(longs.get(i));
                }

                list.add(statisticsVo);
            }
        }

        return list;
    }

    /**
     * 推送实时数据到前台
     * @param rcsMatchDimensionStatistics
     * @return
     */
    @Override
    public Boolean sendSysnData(RcsMatchDimensionStatistics rcsMatchDimensionStatistics){
        log.info("开始推送实时数据到前台：matchId:" + rcsMatchDimensionStatistics.getMatchId() );
        //RcsMatchDimensionStatistics rcsMatchDimensionStatistics = getById(matchId);
        //%100
        if(rcsMatchDimensionStatistics.getTotalValue() == null){
            rcsMatchDimensionStatistics.setTotalValue(BigDecimal.ZERO);
        }
        if(rcsMatchDimensionStatistics.getTotalOrderNums() == null){
            rcsMatchDimensionStatistics.setTotalValue(BigDecimal.ZERO);
        }
        if(rcsMatchDimensionStatistics.getSettledRealTimeValue() == null){
            rcsMatchDimensionStatistics.setSettledRealTimeValue(BigDecimal.ZERO);
        }
        if(rcsMatchDimensionStatistics.getSettledProfitValue() == null){
            rcsMatchDimensionStatistics.setSettledProfitValue(BigDecimal.ZERO);
        }

        //赋值
        rcsMatchDimensionStatistics.setSettledRealTimeValue(rcsMatchDimensionStatistics.getSettledRealTimeValue().divide(BigDecimal.valueOf(100)));
        rcsMatchDimensionStatistics.setSettledProfitValue(rcsMatchDimensionStatistics.getSettledProfitValue().divide(BigDecimal.valueOf(100)));
        rcsMatchDimensionStatistics.setTotalValue(rcsMatchDimensionStatistics.getTotalValue().divide(BigDecimal.valueOf(100)));
        rcsMatchDimensionStatistics.setSettledProfitValue(rcsMatchDimensionStatistics.getSettledProfitValue().divide(BigDecimal.valueOf(100)));

        marketViewService.updateMatchBetChange(rcsMatchDimensionStatistics);
        log.info("结束推送实时数据到前台：matchId:" + rcsMatchDimensionStatistics.getMatchId() );
        return true;
    }

    /**
     * 添加
     * @param rcsMatchDimensionStatistics
     */
    @Override
    public void insertOrSave(RcsMatchDimensionStatistics rcsMatchDimensionStatistics){
        mapper.insertOrSave(rcsMatchDimensionStatistics);
    }
}
