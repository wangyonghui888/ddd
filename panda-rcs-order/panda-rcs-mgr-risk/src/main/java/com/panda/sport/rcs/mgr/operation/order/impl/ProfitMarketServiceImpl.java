package com.panda.sport.rcs.mgr.operation.order.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mgr.aspect.RcsLockSeriesTypeEnum;
import com.panda.sport.rcs.mgr.aspect.RcsLockable;
import com.panda.sport.rcs.mgr.operation.calc.CalcProfitRectangleAdapter;
import com.panda.sport.rcs.mgr.operation.order.CalcOrder;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.utils.MarketValueUtils;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsProfitMarketService;
import com.panda.sport.rcs.profit.utils.ProfitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;


import static com.panda.sport.rcs.profit.utils.ProfitUtil.checkAsianHandicap;


/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation.order.impl
 * @Description :  盘口级别实货值
 * 针对玩法：15:全场单双
 * 大小球: 2:全场大小 18进球数大小-上半场Halftime - Total* Asian total first half
 * 让球: 4: 全场让球 19:亚盘让球-上半场
 * 双方都进球:12双方是否都进球 42：上半场单双
 * 单双
 * @Date: 2019-12-11 14:01
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@Order(200)
public class ProfitMarketServiceImpl extends CalcOrderBase implements CalcOrder {
    /**
     * 滚球标志
     */
    private final Integer scoreMatchTypeFlag = 2;
    @Autowired
    private RcsProfitMarketService rcsProfitMarketService;


    @Autowired
    private StandardSportMarketService sportMarketService;


    @Autowired
    private CalcProfitRectangleAdapter calcProfitRectangleAdapter;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    /**
     * @Description  处理
     * @Param [orderBean]
     * @Author  toney
     * @Date  15:10 2019/12/11
     * @return void
     **/
    @Override
    @Transactional
    //@RcsLockable(key = "profit_market_service",seriesType = RcsLockSeriesTypeEnum.Single)
    public void orderHandle(OrderBean orderBean, Integer type) {
        //拒单
        if(orderBean.getOrderStatus()!=1){
            log.warn("::{}::期望值接收-期望详情-拒单mq，不在计算",orderBean.getOrderNo());
            return;
        }
        //只取单条数据,不要串关的
        if (orderBean.getSeriesType() != 1) {
            log.warn("::{}::盘口值：串关数据不处理" , orderBean.getOrderNo());
            return;
        }
        log.info("::{}::期望值详情过程数据处理",orderBean.getOrderNo());

        for(OrderItem orderItem:orderBean.getItems()){
            if(ProfitUtil.checkIsHandicap(orderItem.getPlayId())) {
//            	RcsProfitMarket bean = calc(orderItem);
//            	if(bean != null) {
//            		//统计到rcs_profit_rectangle
//                    try {
//                        calcProfitRectangleAdapter.calc(orderBean, bean);
//                    }catch (Exception ex){
//                        log.error("期望详情marketValue",ex.getMessage(),ex);
//                    }
//            	}

            	calcProfitRectangleAdapter.calc(orderBean, null, type);
            }
        }
    }



    /**
     * 保存到缓存中
     * @param rcsProfitMarket
     */
    private void saveRedis(RcsProfitMarket rcsProfitMarket){
    	String key = String.format("rcs:profit:match:%s:%s:%s",
    			rcsProfitMarket.getMatchId(),rcsProfitMarket.getMatchType(),rcsProfitMarket.getPlayId());
        redisClient.hSet(key,  rcsProfitMarket.getMarketValue(), JSONObject.toJSONString(rcsProfitMarket));

        producerSendMessageUtils.sendMessage("MYSQL_Profit_Market",rcsProfitMarket);
    }

    /**
     * 获取数据
     * @param orderItem
     * @return
     */
    private RcsProfitMarket getRcsProfitMarket(OrderItem orderItem){
       String json= redisClient.get(String.format("rcs:profit:match:%s:%s:%s:%s", orderItem.getMatchId(),orderItem.getMatchType(),orderItem.getPlayId(),orderItem.getMarketValue()));
       if(StringUtils.isEmpty(json)){
           RcsProfitMarket rcsProfitMarket = getRcsProfitMarketByDb(orderItem);
           if(rcsProfitMarket!=null) {
               saveRedis(rcsProfitMarket);
           }else{
               rcsProfitMarket = initRcsProfitMarket(orderItem);
           }
           return rcsProfitMarket;
       }else{
           return JSONObject.parseObject(json,RcsProfitMarket.class);
       }
    }

    /**
     * 判断是否包含玩法
     * @param playId
     * @return
     */
    private Boolean checkPlay(Integer playId){
        return ProfitUtil.checkIsHandicap(playId);
    }


    /**
     * 计算
     * @param bean
     */
    private RcsProfitMarket calc(OrderItem bean) {
        OrderItem item = new OrderItem();
        BeanUtils.copyProperties(bean, item);

        //判断玩法是不是需要做矩阵计算
        if (!checkPlay(bean.getPlayId())) {
            log.error("期望值传入玩法不对", "统计期望详情报错", JsonFormatUtils.toJson(bean));
            return null;
        }

        //获取对象
        RcsProfitMarket rcsProfitMarket = getRcsProfitMarket(item);
         if (ProfitUtil.checkGoalLine(item.getPlayId())) {
            //大小球
            HandleGoalLine( item, rcsProfitMarket);
        } else if (checkAsianHandicap(item.getPlayId())) {
            //让球
            HandleAsianHandicap( item, rcsProfitMarket);
        }


        rcsProfitMarket.setProfitValue(rcsProfitMarket.getAddition1().add(rcsProfitMarket.getAddition2()));
        rcsProfitMarket.setUpdateTime(new Date());

        //保存到缓存中
        saveRedis(rcsProfitMarket);

        log.info("订单号：" + item.getOrderNo() + ";期望详情插入数据:" + JsonFormatUtils.toJson(rcsProfitMarket));
        return rcsProfitMarket;
    }
    /**
     * @Description  获取盘口值，滚球
     * @Param [orderItem,marketValue]
     * @Author  toney
     * @Date  16:51 2020/4/18
     * @return java.lang.Double
     **/
    private Double getMarketValue(OrderItem orderItem,Double marketValue){
        StandardSportMarket standardSportMarket = sportMarketService.getById(orderItem.getMarketId());
        if (standardSportMarket == null) {
            log.warn("::{}::期望值没有到到market id{}", orderItem.getOrderNo(), JsonFormatUtils.toJson(orderItem));
        }else {
            if (StringUtils.isNotEmpty(standardSportMarket.getAddition2())) {
                try {
                    marketValue = Double.parseDouble(standardSportMarket.getAddition2());
                } catch (Exception ex) {
                    log.error("::{}::期望值错误订单:{}" + orderItem.getOrderNo(), ex.getMessage(),ex);
                }
            }
        }

        return marketValue;
    }

    /**
     * @Description   亚洲让分盘
     * 让球: 4: 全场让球 19:亚盘让球-上半场
     * @Param [item, oddsType, rcsProfitMarket, profitValue]
     * @Author  toney
     * @Date  13:39 2019/12/12
     * @return void
     **/
    private void HandleAsianHandicap(OrderItem item,RcsProfitMarket rcsProfitMarket){
        BigDecimal profitValue = item.getBetAmount1().subtract(item.getPaidAmount1());
        if("1".equals(item.getPlayOptions())){
            //主队让球
            rcsProfitMarket.setAddition1(rcsProfitMarket.getAddition1().
                    add(profitValue));
            rcsProfitMarket.setAddition2(rcsProfitMarket.getAddition2().
                    add(item.getBetAmount1()));
        }else if("2".equals(item.getPlayOptions())){
            //客队让球
            rcsProfitMarket.setAddition1(rcsProfitMarket.getAddition1().
                    add(item.getBetAmount1()));
            rcsProfitMarket.setAddition2(rcsProfitMarket.getAddition2().
                    add(profitValue));
        }
    }

    /**
     * @Description    * 大小球 2:全场大小 18进球数大小-上半场
     * Over大，Under小
     * @Param [betAmount, oddsType, rcsProfitMarket, profitValue]
     * @Author  toney
     * @Date  16:48 2019/12/16
     * @return void
     **/
    private void HandleGoalLine(OrderItem item,RcsProfitMarket rcsProfitMarket) {
        BigDecimal profitValue = item.getBetAmount1().subtract(item.getPaidAmount1());
        if ("Over".equalsIgnoreCase(item.getPlayOptions())) {
            //大
            rcsProfitMarket.setAddition1(rcsProfitMarket.getAddition1().
                    add(profitValue));
            rcsProfitMarket.setAddition2(rcsProfitMarket.getAddition2().
                    add(item.getBetAmount1()));
        } else if ("Under".equalsIgnoreCase(item.getPlayOptions())) {
            //小
            rcsProfitMarket.setAddition1(rcsProfitMarket.getAddition1().
                    add(item.getBetAmount1()));
            rcsProfitMarket.setAddition2(rcsProfitMarket.getAddition2().
                    add(profitValue));
        }
    }



    /**
     * 获取rcsProfitMarket
     * @param item
     * @return
     */
    private RcsProfitMarket getRcsProfitMarketByDb(OrderItem item)
    {
        Double marketValue = MarketValueUtils.mergeMarket(item.getMarketValue());
        if(item.getMatchType().equals(scoreMatchTypeFlag)){
            marketValue = getMarketValue(item,marketValue);
        }

        QueryWrapper<RcsProfitMarket> query = new QueryWrapper<>();
        query.eq("match_id", item.getMatchId());
        query.eq("play_id", item.getPlayId());
        query.eq("market_value", String.valueOf(marketValue));
        query.eq("match_type", String.valueOf(item.getMatchType()));
        return(rcsProfitMarketService.getOne(query));
    }
    /**
     * 初始化
     * @param item
     * @return
     */
    private RcsProfitMarket initRcsProfitMarket(OrderItem item){
        Double marketValue = MarketValueUtils.mergeMarket(item.getMarketValue());
        if(item.getMatchType().equals(scoreMatchTypeFlag)){
            marketValue = getMarketValue(item,marketValue);
        }

        RcsProfitMarket rcsProfitMarket = new RcsProfitMarket();

        rcsProfitMarket.setMatchId(item.getMatchId());
        rcsProfitMarket.setPlayId(item.getPlayId());
        rcsProfitMarket.setMarketValue(String.valueOf(marketValue));
        rcsProfitMarket.setMatchType(item.getMatchType().toString());


        rcsProfitMarket.setAddition1(BigDecimal.ZERO);
        rcsProfitMarket.setAddition2(BigDecimal.ZERO);
        rcsProfitMarket.setProfitValue(BigDecimal.ZERO);
        rcsProfitMarket.setCreateTime(new Date());
        return rcsProfitMarket;
    }
}
