package com.panda.sport.rcs.mgr.wrapper.statistics.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.mapper.statistics.RcsProfitMarketMapper;
import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsProfitMarketService;
import com.panda.sport.rcs.utils.MarketValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.statistics.impl
 * @Description :  盘口级别
 * @Date: 2019-12-11 15:38
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsProfitMarketServiceImpl extends ServiceImpl<RcsProfitMarketMapper, RcsProfitMarket> implements RcsProfitMarketService {
 @Autowired
 private RcsProfitMarketMapper profitMarketMapper;
    /**
     * @Description   更新
     * @Param [rcsProfitMarket]
     * @Author  toney
     * @Date  17:34 2020/2/6
     * @return java.lang.Integer
     **/
    @Override
    public Integer update(RcsProfitMarket rcsProfitMarket){
        return profitMarketMapper.update(rcsProfitMarket);
    }

    /**
     * 添加或者新增
     * @param rcsProfitMarket
     * @return
     */
    @Override
    public Integer insertOrUpdate(RcsProfitMarket rcsProfitMarket){
        return profitMarketMapper.insertOrUpdate(rcsProfitMarket);
    }

    /**
     * 获取market初始数据
     * @param orderItem
     * @return
     */
    @Override
    public RcsProfitMarket getInitProfitMarket(OrderItem orderItem){
        return profitMarketMapper.getInitProfitMarket(getRcsProfitMarket(orderItem),orderItem.getOrderNo());
    }

    /**
     * 给数据赋值
     * @param orderItem
     * @return
     */
    private RcsProfitMarket getRcsProfitMarket(OrderItem orderItem){
        RcsProfitMarket rcsProfitMarket = new RcsProfitMarket();
        rcsProfitMarket.setMatchId(orderItem.getMatchId());
        rcsProfitMarket.setPlayId(orderItem.getPlayId());
        rcsProfitMarket.setMatchType(orderItem.getMatchType().toString());
        if(StringUtils.isNotEmpty(orderItem.getMarketValueNew())){
            rcsProfitMarket.setMarketValue(MarketValueUtils.mergeMarket(orderItem.getMarketValueNew()).toString());
        }else{
            rcsProfitMarket.setMarketValue(MarketValueUtils.mergeMarket(orderItem.getMarketValue()).toString());
        }

        return rcsProfitMarket;
    }



    @Override
    public RcsProfitMarket get(OrderItem orderItem){
        return profitMarketMapper.get(getRcsProfitMarket(orderItem));
    }
}
