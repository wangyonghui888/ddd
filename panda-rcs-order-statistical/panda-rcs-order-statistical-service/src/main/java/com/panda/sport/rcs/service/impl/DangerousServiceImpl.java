package com.panda.sport.rcs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.rcs.common.constants.Constants;
import com.panda.sport.rcs.common.enums.DangerousEnum;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.vo.rule.DangerousR4Vo;
import com.panda.sport.rcs.common.vo.rule.DangerousRuleParameterVo;
import com.panda.sport.rcs.common.vo.rule.OrderDetailVo;
import com.panda.sport.rcs.customdb.entity.MarketEntity;
import com.panda.sport.rcs.customdb.entity.MarketOptionEntity;
import com.panda.sport.rcs.customdb.service.IMarketOptionService;
import com.panda.sport.rcs.customdb.service.IOddConversionServiceExt;
import com.panda.sport.rcs.customdb.service.IOrderDetailService;
import com.panda.sport.rcs.customdb.service.IOrderOptionOddChangeExtService;
import com.panda.sport.rcs.db.entity.OrderOptionOddChange;
import com.panda.sport.rcs.db.entity.UserProfileOrderDangerous;
import com.panda.sport.rcs.db.service.IOrderOptionOddChangeService;
import com.panda.sport.rcs.db.service.IUserProfileOrderDangerousService;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.IDangerousRuleDataService;
import com.panda.sport.rcs.service.IDangerousService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 危险投注规则  逻辑实现
 *
 * @author :  lithan
 * @date: 2020-07-03 11:23:16
 */
@Service
public class DangerousServiceImpl implements IDangerousService {

    Logger log = LoggerFactory.getLogger(UserVisitServiceImpl.class);

    /***  一分钟有多少毫秒 ***/
    private final int milliSecondMinutes = 60 * 1000;

    @Autowired
    RedisService redisService;

    @Autowired
    IOrderDetailService orderDetailService;

    @Autowired
    IDangerousRuleDataService dangerousRuleDataService;

    @Autowired
    IUserProfileOrderDangerousService userProfileOrderDangerousService;

    @Autowired
    IMarketOptionService marketOptionService;

    @Autowired
    IOrderOptionOddChangeService orderOptionOddChangeService;

    @Autowired
    IOrderOptionOddChangeExtService orderOptionOddChangeExtService;


    @Autowired
    IOddConversionServiceExt oddConversionServiceExt;

    /**
     * D1	蛇单投注
     * "赛前阶段，同时满足以下2个条件的注单，标记为“蛇单投注”：
     * 1、注单金额>=1000元（参数1）
     * 2、下注10秒内（参数2）赔率跳水到Y，然后60秒内（参数3）赔率没有回到原值X"
     * @param vo
     */
    @Override
    public void d1(DangerousRuleParameterVo vo) {
        try {

            /*** 注单金额>=1000元（参数1） ***/
            int betAmount = Integer.parseInt(vo.getParameter1());
            //多少秒内 参数2
            long timeScope = Integer.parseInt(vo.getParameter2());
            //得到10秒内 和现在的时间
            Long endTime = System.currentTimeMillis();
            Long startTime = endTime - timeScope * 1000;
            /*** 1、注单金额>=1000元（参数1）2:  2:下注10秒内（参数2）赔率跳水到Y ***/
            List<OrderOptionOddChange> preOrder = orderDetailService.queryPreMatchOrder(startTime, endTime, betAmount);
            if (preOrder.size() > 0 || LocalDateTime.now().getSecond() == 0) {
                log.info("蛇单d1需要处理的订单:" + JSONObject.toJSONString(preOrder));
            }
            if (CollectionUtils.isNotEmpty(preOrder)) {
                orderOptionOddChangeExtService.saveOrUpdate(preOrder);
            }


            // 然后60秒内（参数3） 时间分界线  订单在此时间之前 如果没有回到过原值 则此订单标记为蛇单
            long beginTime = System.currentTimeMillis() - (Long.parseLong(vo.getParameter3()) * 1000);
            QueryWrapper<OrderOptionOddChange> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(OrderOptionOddChange::getMark, 0);
            queryWrapper.lambda().eq(OrderOptionOddChange::getOrderType, 0);
            //查询5分钟前所有的注单
            queryWrapper.lambda().ge(OrderOptionOddChange::getBetTime, beginTime - 5 * 60 * 1000);
            //拿到所有有跳水的注单 并且是未处理的
            List<OrderOptionOddChange> checkedOrdersList = orderOptionOddChangeService.list(queryWrapper);
            if (checkedOrdersList.size() > 0 || LocalDateTime.now().getSecond() == 0) {
                log.info("蛇单d1判断60秒跳水需要处理的订单:" + JSONObject.toJSONString(checkedOrdersList));
            }
            if (CollectionUtils.isEmpty(checkedOrdersList)) {
                return;
            }
            /*** 查找所有相关的投注项 ***/
            Set<Long> set = checkedOrdersList.stream().map(OrderOptionOddChange::getPlayOptionsId).collect(Collectors.toSet());
            Map<Long, MarketOptionEntity> map = getMarketOptions(set);

            //检查这些跳水的注单是否回到了原值
            for (OrderOptionOddChange order : checkedOrdersList) {
                if (order.getBetTime() < beginTime) {
                    log.info("D1超时状态未处理,标记为蛇单:{}", JSONObject.toJSONString(order));
                    order.setRemark("D1超时状态未处理:,标记为蛇单D1:" + (Long.parseLong(vo.getParameter3())));
                    order.setMark(1);
                    orderOptionOddChangeService.updateById(order);
                    //保存蛇单
                    modifyOrderDangerous(CopyUtils.clone(order,OrderDetailVo.class),DangerousEnum.SNAKE.getId());
                    log.info("D1超时状态未处理,标记为蛇单:记录完成,{}", JSONObject.toJSONString(order));
                }
                //如果当前赔率>=投注时的赔率 表示回到了原值 那么就不满足条件  此单不是蛇单 //标记已处理即可
                else if (order.getBetTime() > beginTime && map.get(order.getPlayOptionsId()).getOddsValue() > order.getOddsValue()) {
                    //标记
                    log.info("D1发现赔率回到原值以上:{},{}", JSONObject.toJSONString(order), map.get(order.getPlayOptionsId()).getOddsValue());
                    order.setRemark("D1发现赔率回到原值以上:"+order.getOddsValue()+":"+map.get(order.getPlayOptionsId()).getOddsValue()+"");
                    order.setMark(1);
                    orderOptionOddChangeService.updateById(order);
                    log.info("D1发现赔率回到原值以上:记录完成,{}", JSONObject.toJSONString(order));
                }else {
                    log.info("D1订单未超时,赔率未回到原值{},{}", order.getOrderNo(), order.getBetNo());
                }
            }
        } catch (Exception e) {
            log.error("d1赛前盘订单 计算危险订单出错", e);
        }

    }

    @Override
    public void d2(DangerousRuleParameterVo vo) {

        try {
            //注单金额>=1000元（参数1）
            int betAmount = Integer.parseInt(vo.getParameter1());
            // （参数4） 赔率最小值   表中存放的赔率数据是实际的赔率的十万倍
            int orderOddsValue = new BigDecimal(vo.getParameter4()).multiply(new BigDecimal("100000")).intValue();
            //多少秒内 参数2
            long timeScope = Integer.parseInt(vo.getParameter2());
            long endTime = System.currentTimeMillis();
            long startTime = endTime - timeScope * 1000;
            List<OrderOptionOddChange> liveOrderList = orderDetailService.queryLiveMatchOrder(startTime, endTime, orderOddsValue, betAmount);
            if (liveOrderList.size() > 0 || LocalDateTime.now().getSecond() == 0) {
                log.info("蛇单d2水需要处理的订单:" + JSONObject.toJSONString(liveOrderList));
            }
            if (!CollectionUtils.isEmpty(liveOrderList)) {
                //投注项ID 赔率 盘口id
                Set<Long> optionIds = liveOrderList.stream().map(OrderOptionOddChange::getPlayOptionsId).collect(Collectors.toSet());
                List<MarketOptionEntity> marketOptions = marketOptionService.getMarketOptionByIds(optionIds);
                Map<Long, MarketOptionEntity> marketOptionEntityMap = marketOptions.stream().collect(Collectors.toMap(MarketOptionEntity::getId, Function.identity()));
                log.info("蛇单d2水需要处理的投注项ID:"+JSONObject.toJSONString(marketOptions));
                //盘口id 盘口值
                Set<Long> marketIds = marketOptions.stream().map(MarketOptionEntity::getMarketId).collect(Collectors.toSet());
                List<MarketEntity> markets = marketOptionService.getMarketByIds(marketIds);
                Map<Long, MarketEntity> marketEntityMap = markets.stream().collect(Collectors.toMap(MarketEntity::getId, Function.identity()));
                log.info("蛇单d2水需要处理的盘口ID:"+JSONObject.toJSONString(markets));

                //判断注单欧赔>=supremacy（supremacy=球头-(1-马来赔)   ） 【注：若马来赔为负，则supremacy=球头-(1+马来赔)】
                List<OrderOptionOddChange> orderOptionOddChangeList = new ArrayList<>();
                for (OrderOptionOddChange orderOptionOddChange : liveOrderList) {
                    MarketOptionEntity marketOptionEntity = marketOptionEntityMap.get(orderOptionOddChange.getPlayOptionsId());
                    MarketEntity marketEntity = marketEntityMap.get(marketOptionEntity.getMarketId());
                    boolean flag = compareToSupremacy(marketEntity.getMarketValue(), orderOptionOddChange.getOddsValue());
                    if(flag){
                        orderOptionOddChangeList.add(orderOptionOddChange);
                    }
                }
                log.info("蛇单d2过滤supremacy后需要处理的订单:"+JSONObject.toJSONString(orderOptionOddChangeList));
                if (CollectionUtils.isNotEmpty(orderOptionOddChangeList)) {
                    orderOptionOddChangeExtService.saveOrUpdate(orderOptionOddChangeList);
                }
            }



            // 然后30秒内（参数3） 时间分界线  订单在此时间之前 如果没有回到过原值 则此订单标记为蛇单
            long beginTime = System.currentTimeMillis() - (Long.parseLong(vo.getParameter3()) * 1000);
            QueryWrapper<OrderOptionOddChange> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(OrderOptionOddChange::getMark, 0);
            queryWrapper.lambda().eq(OrderOptionOddChange::getOrderType, 1);
            //查询5分钟前所有的注单
            queryWrapper.lambda().ge(OrderOptionOddChange::getBetTime, beginTime - 5 * 60 * 1000);
            //拿到所有有跳水的注单 并且是未处理的
            List<OrderOptionOddChange> checkedOrdersList = orderOptionOddChangeService.list(queryWrapper);
            if (checkedOrdersList.size() > 0 || LocalDateTime.now().getSecond() == 0) {
                log.info("蛇单d2判断30秒跳水需要处理的订单:"+JSONObject.toJSONString(checkedOrdersList));
            }
            if (CollectionUtils.isEmpty(checkedOrdersList)) {
                return;
            }
            //检查这些跳水的注单是否回到了原值
            for (OrderOptionOddChange order : checkedOrdersList) {
                Set<Long> downSet = new HashSet<>();
                downSet.add(order.getPlayOptionsId());
                int currOddsValue = marketOptionService.getMarketOptionByIds(downSet).get(0).getOddsValue();

                if (order.getBetTime() < beginTime) {
                    log.info("蛇单d2超时状态未处理,标记为蛇单:{}", JSONObject.toJSONString(order));
                    order.setRemark("D2超时状态未处理:,标记为蛇单D2" + (Long.parseLong(vo.getParameter3())));
                    order.setMark(1);
                    orderOptionOddChangeService.updateById(order);
                    //保存蛇单
                    modifyOrderDangerous(CopyUtils.clone(order,OrderDetailVo.class),DangerousEnum.SNAKE.getId());
                    log.info("蛇单d2超时状态未处理,标记为蛇单:记录完成,{}", JSONObject.toJSONString(order));
                }
                //如果当前赔率>=投注时的赔率 表示回到了原值 那么就不满足条件  此单不是蛇单 //标记已处理即可
                else if (order.getBetTime() > beginTime && currOddsValue > order.getOddsValue()) {
                    //标记
                    log.info("蛇单d2发现赔率回到原值以上:{},{}", JSONObject.toJSONString(order), currOddsValue);
                    order.setRemark("D2发现赔率回到原值以上:" + order.getOddsValue() + ":" + currOddsValue);
                    order.setMark(1);
                    orderOptionOddChangeService.updateById(order);
                    log.info("蛇单d2发现赔率回到原值以上:记录完成,{}", JSONObject.toJSONString(order));
                }else {
                    log.info("D2订单未超时,赔率未回到原值{},{}", order.getOrderNo(), order.getBetNo());
                }
            }

        } catch (Exception e) {
            log.error("D2滚球盘订单 计算危险订单出错", e);
        }


    }



    private Map<Long, MarketOptionEntity> getMarketOptions(Set<Long> optionIds) {
        if (CollectionUtils.isEmpty(optionIds)) {
            return null;
        }
        List<MarketOptionEntity> marketOptionList = marketOptionService.getMarketOptionByIds(optionIds);
        Map<Long, MarketOptionEntity> map = new HashMap<>();
        marketOptionList.forEach(m->{
            map.put(m.getId(), m);
        });
        return map;
    }
    /***
     * 判断盘口值为      marketValue,欧赔为 euOddsValue(实际赔率乘10万) 的投注项 是否满足 条件
     * ( 5、注单欧赔>=supremacy（supremacy=球头-(1-马来赔)   ）【注：若马来赔为负，则supremacy=球头-(1+马来赔)】 );
     * @param marketValueStr
     * @param euOddsValue
     * @return boolean
     * @Description
     * @Author dorich
     * @Date 10:56 2020/7/21
     **/
    private boolean compareToSupremacy(String marketValueStr, int euOddsValue) {
        // 把乘 10万的赔率转换为展示的赔率
        BigDecimal euOdd = new BigDecimal(euOddsValue).divide(BigDecimal.valueOf(100000)).setScale(2,BigDecimal.ROUND_DOWN);
        //转成马来赔率
        String malaysiaOddStr = oddConversionServiceExt.getEuOddByMaOdd(euOdd.toString());
        BigDecimal malaysiaOddValue = new BigDecimal(malaysiaOddStr);

        //盘口值
        BigDecimal marketValue = BigDecimal.ZERO;
        //获取盘口值得
        if (marketValueStr.contains("/")) {
            String[] values = marketValueStr.split("/");
            marketValue = new BigDecimal(values[0]).add(new BigDecimal(values[1])).divide(BigDecimal.valueOf(2)).setScale(2,BigDecimal.ROUND_DOWN);
        } else {
            marketValue = new BigDecimal(marketValueStr);
        }

        //注单欧赔>=supremacy（supremacy=球头-(1-马来赔)   ） 【注：若马来赔为负，则supremacy=球头-(1+马来赔)】
        if(malaysiaOddValue.compareTo(BigDecimal.ZERO)<0){
            malaysiaOddValue = malaysiaOddValue.multiply(new BigDecimal("-1"));
        }
        BigDecimal supremacy = new BigDecimal("1").subtract(malaysiaOddValue);
        supremacy = marketValue.subtract(supremacy);

        if (euOdd.compareTo(supremacy) > 0) {
            return true;
        }
        return false;
    }



    /***
     * 标记参数指定的订单为已处理订单
     * @param orderOptionOddChanges
     * @return void
     * @Description
     * @Author dorich
     * @Date 16:16 2020/7/19
     **/
    private void markOrderOptionChange(List<OrderOptionOddChange> orderOptionOddChanges) {
        Set<Long> orderIds = orderOptionOddChanges.stream().map(OrderOptionOddChange::getId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(orderIds)) {
            return;
        }
        /*** 更新已经处理过的订单信息 ***/
        /*** 标记   中的订单信息为不在处理(60秒已过) ***/
        UpdateWrapper<OrderOptionOddChange> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(OrderOptionOddChange::getId, orderIds);
        updateWrapper.lambda().set(OrderOptionOddChange::getMark, 1);
        orderOptionOddChangeService.update(updateWrapper);
        log.info("此次更新赛前盘订单个数:" + orderIds.size());
    }

    /**
     * D2	打水投注	"满足以下任一条件的均为打水投注：
     * 1.单账户的同投注项的两笔或多笔注单，间隔时间少于2秒（参数1）
     * 2.投注项1出现蛇单时，5秒内（参数2）购买投注项2的注单（无论是否投注成功）
     * 3.同一赛事注单达到参数3笔，且至少参数3笔注单的下注时间间隔相似（差异在参数4秒内），这些下注时间间隔相似的注单标记标记为打水投注"
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void d3(DangerousRuleParameterVo vo) {
        log.info("水单判断开始:{}",vo.getOrderDetailVo().getOrderNo());
        // 1.单账户的同投注项的两笔或多笔注单，间隔时间少于2秒（参数1）
        Long matchId = vo.getOrderDetailVo().getMatchId();
        Integer playId = vo.getOrderDetailVo().getPlayId();
        Long playOptionsId = vo.getOrderDetailVo().getPlayOptionsId();
        Long userId = Long.valueOf(vo.getUserId());
        Long marketId = Long.valueOf(vo.getOrderDetailVo().getMarketId());
        //获取和这笔订单 相同投注项的注单
        List<OrderDetailVo> list = dangerousRuleDataService.getOrderByPlayOptions(matchId, playId, playOptionsId, userId);
        log.info("水单1判断开始 列表:{},{}",vo.getOrderDetailVo().getOrderNo(),list.size());
        //判断规则
        boolean flag = checkOrderOptions(list, vo);
        log.info("水单1检查完成:{},{}",vo.getOrderDetailVo().getOrderNo(),flag);
        if (flag) {
            modifyOrderDangerous(vo.getOrderDetailVo(), DangerousEnum.WATER.getId());
            log.info("水单1:{}",vo.getOrderDetailVo().getOrderNo());
            return;
        }
        log.info("水单2开始:{}",vo.getOrderDetailVo().getOrderNo());
        //2.投注项1出现蛇单时，5秒内（参数2）购买投注项2的注单（无论是否投注成功）
        String playIds ="7,8,9,13,14,20,21,22,23,31,35,36,68,73,74,101,102,103,104,105,106,107,117,136,141,148,150,151,152,159,161,166,170,171,174,204,209,210,211,212,216,218,223,226,227,228,236,238,239,241";
        List playIdlist = Arrays.asList(playIds.split(","));
        if(!playIdlist.contains(vo.getOrderDetailVo().getPlayId().toString())){
            Long betTime = vo.getOrderDetailVo().getBetTime();
            Long second = Long.valueOf(vo.getParameter2());
            Long beginTime = betTime - (second * 5000);
            //同盘口 其他投注项 5秒内有蛇单  此单就满足条件
            LambdaQueryWrapper<UserProfileOrderDangerous> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserProfileOrderDangerous::getMarketId, marketId);
            wrapper.eq(UserProfileOrderDangerous::getDangerousId, DangerousEnum.SNAKE.getId());
            wrapper.ne(UserProfileOrderDangerous::getPlayOptionsId, playOptionsId);
            wrapper.gt(UserProfileOrderDangerous::getCreateTime, beginTime);
            wrapper.lt(UserProfileOrderDangerous::getCreateTime, betTime);
            List<UserProfileOrderDangerous> dangerousList = userProfileOrderDangerousService.list(wrapper);
            if (dangerousList.size() > 0) {
                modifyOrderDangerous(vo.getOrderDetailVo(), DangerousEnum.WATER.getId());
                log.info("水单2:{}",vo.getOrderDetailVo().getOrderNo());
                return;
            }
        }
        log.info("水单3开始:{}",vo.getOrderDetailVo().getOrderNo());
        //3.同一赛事注单达到参数3笔，且至少参数3笔注单的下注时间间隔相似（差异在参数4秒内），这些下注时间间隔相似的注单标记标记为打水投注
        List<OrderDetailVo> orderList = dangerousRuleDataService.getOrderByMatchId(matchId, userId);
        //得到需要标记为打水的注单
        List<OrderDetailVo> filterList = checkOrderMatch(orderList, vo);
        //更新数据库
        for (OrderDetailVo orderDetailVo : filterList) {
            modifyOrderDangerous(orderDetailVo, DangerousEnum.WATER.getId());
            log.info("水单3:{}",vo.getOrderDetailVo().getOrderNo());
        }
        log.info("水单结束:{}",vo.getOrderDetailVo().getOrderNo());
    }

    /**
     * D3	资讯投注	注单（无论是否投注成功）投注时间之后的5秒内（参数1）发生了进球、红牌事件
     *
     * @param vo
     */
    @Override
    public void d4(DangerousRuleParameterVo vo) {
        OrderDetailVo orderDetailVo = vo.getOrderDetailVo();
        //多少秒内
        Long second = Long.valueOf(vo.getParameter1());
        Long beginTime = orderDetailVo.getBetTime();
        Long endTime = beginTime + (second * 1000);
        Long matchId = orderDetailVo.getMatchId();
        //指定的事件才算 咨询投注
        String eventCodes = Constants.EVENT_CODES;
        Long num = dangerousRuleDataService.getEventNum(beginTime, endTime, matchId, eventCodes);
        if (num > 0) {
            modifyOrderDangerous(orderDetailVo, DangerousEnum.INFORMATION.getId());
        }
    }

    /**
     * D4	篮球打洞	篮球投注时，在同一赛事相同玩法的不同盘口之间交叉下注（无论是否投注成功），相应注单标记为篮球打洞。
     * 涉及玩法PDID：2（常规赛总分）、10（常规赛{主队}总分）、11（常规赛{客队}总分）、18（上半场总分）、26（下半场总分）、38（总分）、45（第1节总分）、
     * 51（第2节总分）、57（第3节总分）、63（第4节总分）、87（上半场{主队}总分）、88（下半场{主队}总分）、97（上半场{客队}总分）、98（下半场{客队}总分）、
     * 145（第{X}节{主队}总分）、146（第{X}节{客队}总分）
     *
     * @param vo
     */
    @Override
    public void d5(DangerousRuleParameterVo vo) {
        OrderDetailVo detailVo = vo.getOrderDetailVo();
        List<DangerousR4Vo> list = dangerousRuleDataService.getBasketball(detailVo.getMatchId(), detailVo.getPlayId(), detailVo.getUid());
        if (ObjectUtils.isNotEmpty(list)) {
            modifyOrderDangerous(vo.getOrderDetailVo(), DangerousEnum.BASKETBALL.getId());
        }
    }

    /**
     * D5	风控拒单	"满足以下任一条件的均标记为风控拒单：
     * 1.自动操盘被拒绝的注单
     * 2.手动模式拒绝的注单"
     * 订单状态 取自业务表 (0:待处理,1:已处理,2:取消交易【融合】,3:待确认,4:已拒绝【风控】,5:撤单【赛事取消】)
     *
     * @param vo
     */
    @Override
    public void d6(DangerousRuleParameterVo vo) {
        Integer orderStatus = dangerousRuleDataService.getOrderRiskStatus(vo.getOrderDetailVo().getOrderNo());
        if (orderStatus == 4) {
            modifyOrderDangerous(vo.getOrderDetailVo(), DangerousEnum.RISK_REJECTED.getId());
        }
    }

    /**
     * 检查d2-1
     * 1.单账户的同投注项的两笔或多笔注单，间隔时间少于2秒（参数1）
     *
     * @param list
     * @return
     */
    private boolean checkOrderOptions(List<OrderDetailVo> list, DangerousRuleParameterVo vo) {
        //配置的 间隔时间
        Long second = Long.valueOf(vo.getParameter1());
        if (list.size() < 2) {
            return false;
        }
        //排序
        list = list.stream().sorted(Comparator.comparing(OrderDetailVo::getBetTime)).collect(Collectors.toList());
        //对比每个间隔时间 有任何满足条件的即可
        for (int i = 0; i < list.size() - 1; i++) {
            long difference = list.get(i + 1).getBetTime() - list.get(i).getBetTime();
            if (difference <= second * 1000) {
                log.info("{}检测到打水投注(1):订单1:{},订单2:{}", list.size(), JSONObject.toJSONString(list.get(i + 1)), JSONObject.toJSONString(list.get(i)));
                return true;
            }
        }
        return false;
    }

    /**
     * 检查 d2-3
     * 3.同一赛事注单达到参数3笔，且至少参数3笔注单的下注时间间隔相似（差异在参数4秒内），这些下注时间间隔相似的注单标记标记为打水投注
     * 这个规则产品 设计的比较特别  逻辑有点烧脑
     *
     * @param list
     * @return
     */
    private List<OrderDetailVo> checkOrderMatch(List<OrderDetailVo> list, DangerousRuleParameterVo vo) {
        //返回 符合规则的注单
        List<OrderDetailVo> resultList = new ArrayList<>();

        //连续多少笔 参数 间隔相似
        Long num = Long.valueOf(vo.getParameter3());
        //多少秒内 参数
        Long second = Long.valueOf(vo.getParameter4());
        //如果订单长度 没有要求连续数量多  则无需判断
        if (list.size() < num) {
            return new ArrayList<>();
        }
        //排序
        list = list.stream().sorted(Comparator.comparing(OrderDetailVo::getBetTime)).collect(Collectors.toList());

        //临时记录 需要更新为打水的注单
        Set<OrderDetailVo> set = new HashSet<>();
        //连续累加数量
        int continuous = 1;
        //对比每个间隔时间 有任何满足条件的即可
        for (int i = 0; i < list.size() - 1; i++) {
            OrderDetailVo orderThis = list.get(i);
            OrderDetailVo orderNext = list.get(i + 1);
            //两笔订单相差的时间
            long difference = orderNext.getBetTime() - orderThis.getBetTime();
            //如果两笔订单相差的时间 在设置的参数范围内
            if (difference <= second * 1000) {
                set.add(orderThis);
                set.add(orderNext);
                continuous++;
                //最后两笔直接计算
                if (i == list.size() - 2) {
                    if (continuous >= num) {
                        resultList.addAll(set);
                    }
                }
            } else {
                //如果不在范围内 计算之前累积的笔数 是否符合规则
                if (continuous >= num) {
                    resultList.addAll(set);
                }
                continuous = 1;
                set.clear();
            }
        }
        resultList = resultList.stream().sorted(Comparator.comparing(OrderDetailVo::getBetTime)).collect(Collectors.toList());
        return resultList;
    }

    /**
     * 保存危险规则关系
     *
     * @param vo
     * @param dangerousId
     */
    private void modifyOrderDangerous(OrderDetailVo vo, Long dangerousId) {
        LambdaUpdateWrapper<UserProfileOrderDangerous> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserProfileOrderDangerous::getBetNo, vo.getBetNo());
        wrapper.eq(UserProfileOrderDangerous::getOrderNo, vo.getOrderNo());
        wrapper.eq(UserProfileOrderDangerous::getDangerousId, dangerousId);
        List<UserProfileOrderDangerous> queryEntity = userProfileOrderDangerousService.list(wrapper);
        if (ObjectUtils.isEmpty(queryEntity)) {
            UserProfileOrderDangerous entity = new UserProfileOrderDangerous();
            entity.setBetNo(vo.getBetNo());
            entity.setOrderNo(vo.getOrderNo());
            entity.setDangerousId(dangerousId);
            entity.setPlayOptionsId(vo.getPlayOptionsId());
            entity.setMarketId(vo.getMarketId());
            entity.setCreateTime(System.currentTimeMillis());
            userProfileOrderDangerousService.save(entity);
            log.info("注单标记为危险:{}",JSONObject.toJSONString(entity));
        }
    }

    public static void main(String[] args) {
        List<OrderDetailVo> list = new ArrayList<>();
        OrderDetailVo o1 = new OrderDetailVo();
        o1.setBetTime(1000L);
        list.add(o1);
        OrderDetailVo o2 = new OrderDetailVo();
        o2.setBetTime(3000L);
        list.add(o2);
        OrderDetailVo o3 = new OrderDetailVo();
        o3.setBetTime(6000L);
        list.add(o3);
        OrderDetailVo o4 = new OrderDetailVo();
        o4.setBetTime(8000L);
        list.add(o4);
        OrderDetailVo o5 = new OrderDetailVo();
        o5.setBetTime(12000L);
        list.add(o5);
        OrderDetailVo o6 = new OrderDetailVo();
        o6.setBetTime(13000L);
        list.add(o6);
        OrderDetailVo o7 = new OrderDetailVo();
        o7.setBetTime(19000L);
        list.add(o7);
        OrderDetailVo o8 = new OrderDetailVo();
        o8.setBetTime(20000L);
        list.add(o8);
        OrderDetailVo o9 = new OrderDetailVo();
        o9.setBetTime(22000L);
        list.add(o9);
        OrderDetailVo o10 = new OrderDetailVo();
        o10.setBetTime(25000L);
        list.add(o10);
        OrderDetailVo o11 = new OrderDetailVo();
        o11.setBetTime(27000L);
        list.add(o11);
        DangerousRuleParameterVo vo = new DangerousRuleParameterVo();
        vo.setParameter3("6");
        vo.setParameter4("4");
        List<OrderDetailVo> mylist = new DangerousServiceImpl().checkOrderMatch(list, vo);
        for (OrderDetailVo orderDetailVo : mylist) {
            System.out.println(orderDetailVo.getBetTime());
        }
    }

}
