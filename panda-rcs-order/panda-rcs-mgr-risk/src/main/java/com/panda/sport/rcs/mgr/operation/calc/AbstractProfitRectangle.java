package com.panda.sport.rcs.mgr.operation.calc;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mgr.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsProfitMarketService;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsProfitRectangleService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import com.panda.sport.rcs.pojo.statistics.RcsProfitRectangle;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;
import com.panda.sport.rcs.vo.statistics.MarketBalanceVo;
import com.panda.sport.rcs.vo.statistics.MarketProfitVo;
import com.panda.sport.rcs.vo.statistics.ProfitDetailBean;
import com.panda.sport.rcs.vo.statistics.ProfitRectangleMQVo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation.order.impl
 * @Description :  期望值矩阵处理
 * @Date: 2019-12-13 11:57
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public abstract class AbstractProfitRectangle {
    @Autowired
    protected RcsProfitRectangleService rcsProfitRectangleService;
    @Autowired
    protected RcsProfitMarketService rcsProfitMarketService;
    /**
     * redis操作
     */
    @Autowired
    protected RedisClient redisClient;


    /**
     * @return java.lang.Boolean
     * @Description 校验规则
     * @Param []
     * @Author toney
     * @Date 9:49 2019/12/19
     **/
    public abstract Boolean checkParams(OrderItem orderItem);


    @Autowired
    private ITOrderDetailService orderDetailService;


    /**
     * 逻辑处理
     *
     * @param orderItem
     * @param bean
     */
    public abstract ConcurrentHashMap<Double, RcsProfitRectangle> logicHandle(OrderItem orderItem, ConcurrentHashMap<Double, RcsProfitRectangle> map, RcsProfitMarket bean, Integer type);

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;


    /**
     * @param bean
     * @return void
     * @Description 处理
     * @Param [orderBean]
     * @Author toney
     * @Date 15:10 2019/12/11
     **/
    public void handleData(OrderBean orderBean, ConcurrentHashMap<Double, RcsProfitRectangle> map, Double startIndex, Double endIndex, RcsProfitMarket bean, Integer type) {
        OrderItem orderItem = orderBean.getItems().get(0);
        if (checkParams(orderItem)) {
            initRcsProfitRectangle(orderItem, map, startIndex, endIndex);
            map = logicHandle(orderItem, map, bean,type);
            ProfitRectangleMQVo profitRectangleMQVo = new ProfitRectangleMQVo(orderItem.getMatchId(), orderItem.getPlayId(), orderItem.getMatchType(), map);
//            producerSendMessageUtils.sendMessage("MYSQL_Profit_Rectangle", profitRectangleMQVo);
            HashMap<String, String> mqMap = new HashMap<>();
            mqMap.put("time", "" + System.currentTimeMillis());
            String hashKey = String.format("%s_%s_%s",profitRectangleMQVo.getMatchId(),profitRectangleMQVo.getPlayId(),profitRectangleMQVo.getMatchType());
            producerSendMessageUtils.sendMsg("MYSQL_Profit_Rectangle", "", "", JSONObject.toJSONString(profitRectangleMQVo), mqMap, hashKey);
            log.info("::{}::玩法级别MYSQL_Profit_Rectangle推送内容:{}",orderBean.getOrderNo(), JsonFormatUtils.toJson(profitRectangleMQVo));
            syncData(orderItem.getMatchId(), orderItem.getPlayId(), orderItem.getMatchType(), map);
            log.info("::{}::期望详情{}",orderBean.getOrderNo(),JsonFormatUtils.toJson(map.values()));
        }
    }


    /**
     * 初始化矩阵
     *
     * @param orderItem
     * @return
     */
    public ConcurrentHashMap<Double, RcsProfitRectangle> initRcsProfitRectangle(OrderItem orderItem, ConcurrentHashMap<Double, RcsProfitRectangle> map, Double startIndex, Double endIndex) {
        for (Double i = startIndex; i <= endIndex; i++) {
            RcsProfitRectangle rcsProfitRectangle = new RcsProfitRectangle();
            rcsProfitRectangle.setMatchId(orderItem.getMatchId());
            rcsProfitRectangle.setPlayId(orderItem.getPlayId());
            rcsProfitRectangle.setScore(i.intValue());
            rcsProfitRectangle.setProfitValue(BigDecimal.ZERO);
            rcsProfitRectangle.setCreateTime(new Date());
            rcsProfitRectangle.setUpdateTime(new Date());
            rcsProfitRectangle.setMatchType(orderItem.getMatchType());
            map.put(i, rcsProfitRectangle);
        }
        return map;
    }


    /**
     * 计数
     *
     * @param orderItem
     * @param bean
     */
    public void calcData(OrderItem orderItem, ConcurrentHashMap<Double, RcsProfitRectangle> map, Double startIndex, Double endIndex, RcsProfitMarket bean) {

    }


    /**
     * @return void
     * @Description 实时推送数据
     * @Param [orderItem]
     * @Author myname
     * @Date 10:33 2020/1/13
     **/
    private void syncData(Long matchId, Integer playId, Integer matchType, ConcurrentHashMap<Double, RcsProfitRectangle> map) {
        try {
            ProfitDetailBean bean = new ProfitDetailBean();
            bean.setMatchId(matchId);
            bean.setPlayId(playId);
            bean.setMatchType(matchType);
            bean.setBalancesList(getBalanceByMatchIdAndPlayId(matchId, playId.longValue(), matchType));
            //bean.setRcsProfitRectangleList(getProfitByMatchIdAndPlayId(matchId, playId, matchType));
            Iterator<Map.Entry<Double, RcsProfitRectangle>> iterator = map.entrySet().iterator();
            List<MarketProfitVo> rcsProfitRectangles = new ArrayList();
            while (iterator.hasNext()) {
                Map.Entry<Double, RcsProfitRectangle> next = iterator.next();
                RcsProfitRectangle value = next.getValue();
                MarketProfitVo vo = new MarketProfitVo();
                vo.setProfitValue(value.getProfitValue());
                vo.setScore(value.getScore());
                rcsProfitRectangles.add(vo);
            }
            rcsProfitRectangles = rcsProfitRectangles.stream().sorted(Comparator.comparing(MarketProfitVo::getScore).reversed()).collect(Collectors.toList());
            bean.setRcsProfitRectangleList(rcsProfitRectangles);
            producerSendMessageUtils.sendMessage(MqConstants.WS_ODDS_CHANGED_TOPIC, MqConstants.WS_SCROLL_BALL_LIVE_BET_DETAIL_BY_PLAYID_2_4_18_19_CHANGED_TAG, "", bean);
        } catch (Exception ex) {
            log.error("::{}::推送期望详情失败:{}",matchId, ex.getMessage(), ex);
        }
    }


    /**
     * @return java.util.List<com.panda.sport.rcs.vo.statistics.MarketBalanceVo>
     * @Description 获取平衡值
     * @Param [matchId, playId]
     * @Author myname
     * @Date 13:20 2020/1/13
     **/
    private List<MarketBalanceVo> getBalanceByMatchIdAndPlayId(Long matchId, Long playId, Integer matchType) {
        List<OrderDetailStatReportVo> list = orderDetailService.getMarketStatByMatchIdAndPlayId(matchId, playId, matchType);
        List<MarketBalanceVo> oddList = Lists.newArrayList();
        Map<Long, MarketBalanceVo> markets = Maps.newHashMap();
        for (OrderDetailStatReportVo rvo : list) {
            MarketBalanceVo vo = markets.get(rvo.getMarketId());
            if (vo == null) {
                vo = new MarketBalanceVo();
                vo.setMarketId(rvo.getMarketId());
                vo.setMarketValue(rvo.getMarketValue());
                markets.put(rvo.getMarketId(), vo);
            }
            if ("home".equals(rvo.getOddsType())) {
                vo.setHomeAmount(rvo.getBetAmount() / 100);
            } else {
                vo.setAwayAmount(rvo.getBetAmount() / 100 * -1);
            }
            vo.setBalanceValue(vo.getHomeAmount() + vo.getAwayAmount());
        }
        for (Map.Entry<Long, MarketBalanceVo> entry : markets.entrySet()) {
            oddList.add(entry.getValue());
        }
        Collections.sort(oddList, new Comparator<MarketBalanceVo>() {
            @Override
            public int compare(MarketBalanceVo o1, MarketBalanceVo o2) {
                return (int) ((Float.parseFloat(o1.getMarketValue()) - Float.parseFloat(o2.getMarketValue())) * 100);
            }
        });
        return oddList;
    }

    /**
     * @param matchType
     * @return java.util.List<com.panda.sport.rcs.vo.statistics.MarketProfitVo>
     * @Description 获取期望详情数据
     * @Param [rcsProfitRectangle]
     * @Author myname
     * @Date 11:23 2020/1/13
     **/
    private List<MarketProfitVo> getProfitByMatchIdAndPlayId(Long matchId, Integer playId, Integer matchType) {
        QueryWrapper<RcsProfitRectangle> wrapper = new QueryWrapper<>();
        wrapper.eq("match_id", matchId);
        wrapper.eq("play_id", playId);
        wrapper.eq("match_type", matchType);
        wrapper.lambda().orderByDesc(RcsProfitRectangle::getScore);
        List<RcsProfitRectangle> rcsProfitRectangleList = rcsProfitRectangleService.list(wrapper);
        List<MarketProfitVo> list = BeanCopyUtils.copyPropertiesList(rcsProfitRectangleList, MarketProfitVo.class);
        return list;
    }

    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.statistics.RcsProfitMarket>
     * @Description 获取期望详情过程数据
     * @Param [orderItem]
     * @Author toney
     * @Date 12:17 2020/2/10
     **/
    protected List<RcsProfitMarket> getRcsProfitMakeretList(OrderItem orderItem) {
        /*QueryWrapper<RcsProfitMarket> queryWrapper = new QueryWrapper();
        queryWrapper.eq("match_id", orderItem.getMatchId());
        queryWrapper.eq("play_id", orderItem.getPlayId());
        queryWrapper.eq("match_type", orderItem.getMatchType());
        List<RcsProfitMarket> rcsProfitMarketList = rcsProfitMarketService.list(queryWrapper);

        return rcsProfitMarketList;*/
        List<RcsProfitMarket> list = new ArrayList<>();
//       for(String redisName:sets){
//           String json= redisClient.get(redisName);
//           list.add(JSONObject.parseObject(json,RcsProfitMarket.class));
//       }

        String key = String.format(RedisKeys.PROFIT_MARKET_INFO, orderItem.getMatchId(), orderItem.getMatchType(), orderItem.getPlayId());

        Map<String, RcsProfitMarket> map = redisClient.hGetAll(key, RcsProfitMarket.class);
        list.addAll(map.values());

        if (map == null || map.size() <= 0) {
            RcsProfitMarket rcsProfitMarket = new RcsProfitMarket();
            rcsProfitMarket.setAddition1(BigDecimal.ZERO);
            rcsProfitMarket.setAddition2(BigDecimal.ZERO);
            rcsProfitMarket.setProfitValue(BigDecimal.ZERO);
            rcsProfitMarket.setMatchId(orderItem.getMatchId());
            rcsProfitMarket.setPlayId(orderItem.getPlayId());
            rcsProfitMarket.setMatchType(orderItem.getMatchType().toString());
            rcsProfitMarket.setMarketValue(orderItem.getMarketValue());
            list.add(rcsProfitMarket);
        }
        return list;
    }

    public static String getProfitResultResultCacheKey(Long matchId, Integer matchType, Integer playId) {
        return String.format(RedisKeys.PROFIT_DETAIL_RESULT, matchId, matchType, playId);
    }
}
