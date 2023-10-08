package com.panda.sport.rcs.third.service.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.base.Stopwatch;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.MtsIsCacheEnum;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.TOrderMapper;
import com.panda.sport.rcs.mapper.limit.RcsLabelLimitConfigMapper;
import com.panda.sport.rcs.pojo.RcsLabelLimitConfig;
import com.panda.sport.rcs.pojo.TOrder;
import com.panda.sport.rcs.third.config.OrderCacheConfig;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.entity.common.ThirdOrderDelayVo;
import com.panda.sport.rcs.third.entity.common.ThirdResultVo;
import com.panda.sport.rcs.third.entity.common.pojo.ErrorMessagePrompt;
import com.panda.sport.rcs.third.enums.OrderStatusEnum;
import com.panda.sport.rcs.third.factory.ThirdStrategyFactory;
import com.panda.sport.rcs.third.service.reject.IOrderAcceptService;
import com.panda.sport.rcs.third.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OrderInfoStatusEnum;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.panda.sport.rcs.third.common.Constants.*;

/**
 * @author Beulah
 * @date 2023/3/21 19:21
 * @description 订单处理模式
 */

@Service
@Slf4j
public class OrderHandlerServiceImpl implements IOrderHandlerService {


    @Autowired
    TOrderDetailMapper orderDetailMapper;
    @Resource
    RedisClient redisClient;
    @Resource
    IOrderAcceptService orderAcceptService;
    @Resource
    ProducerSendMessageUtils sendMessage;
    @Resource
    RcsLabelLimitConfigMapper labelLimitConfigMapper;
    @Resource
    TOrderMapper orderMapper;
    //接拒逻辑
    @Resource(name = "orderAcceptServiceImpl")
    IOrderAcceptService acceptService;

    //缓存接单概率
    @Resource
    OrderCacheConfig cacheConfig;

    @Resource(name = "confirmPoolExecutor")
    private ThreadPoolExecutor confirmPoolExecutor;
    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;


    /**
     * 走内部接单
     *
     * @param ext 订单
     */
    @Override
    public void orderByPa(ThirdOrderExt ext) {
        String orderNo = ext.getList().get(0).getOrderId();
        String third = ext.getThird();
        log.info("::{}::投注-{}订单执行PA后置检查流程::", orderNo, third);
        //订单最终状态
        int orderStatus = OrderInfoStatusEnum.WAITING.getCode();
        //订单处理状态
        Integer infoStatus = OrderInfoStatusEnum.RISK_PROCESSING.getCode();
        //提示
        String infoMsg = OrderInfoStatusEnum.RISK_PROCESSING.getMode();
        boolean isScroll = orderAcceptService.orderIsScroll(ext.getList());
        int mtsIsCache = getMtsIsCache(third, 3);
        //早盘秒接
        if (!isScroll) {
            log.info("::{}::投注-{}订单PA后置检查-早盘秒接::", orderNo, third);
            infoMsg = OrderInfoStatusEnum.EARLY_PASS.getMode();
            orderStatus = OrderStatusEnum.ACCEPTED.getCode();
            infoStatus = OrderInfoStatusEnum.EARLY_PASS.getCode();
            ext.setOrderStatus(orderStatus);
            updateOrder(ext, infoStatus, infoMsg, mtsIsCache);
            return;
        }
        //滚球是否秒接检查
        if (acceptService.checkScrollSpeedAccept(ext.getList(), third)) {
            log.info("::{}::投注-{}订单PA后置检查-滚球赛事秒接::", orderNo, third);
            infoMsg = "赛事秒接";
            infoStatus = OrderInfoStatusEnum.SCROLL_PASS.getCode();
            orderStatus = OrderStatusEnum.ACCEPTED.getCode();
            ext.setOrderStatus(orderStatus);
            updateOrder(ext, infoStatus, infoMsg, mtsIsCache);
        } else {
            //后置检查
            ErrorMessagePrompt errorMessage = new ErrorMessagePrompt();
            boolean checkStatus = orderAcceptService.dealWithData(ext.getList(), errorMessage, third, OrderStatusEnum.WAITING.getCode());
            ext.setOrderNo(orderNo);
            mtsIsCache = getMtsIsCache(third, 3);
            Integer code = OrderInfoStatusEnum.SCROLL_PASS.getCode();
            String msg = third + "滚球PA接单";
            if (checkStatus) {
                log.info("::{}::投注-{}订单PA后置检查,检查实时接拒信息拒单", orderNo, third);
                ext.setOrderStatus(OrderStatusEnum.REJECTED.getCode());
                code = OrderInfoStatusEnum.SCROLL_REFUSE.getCode();
                msg = third + "滚球PA拒单";
                if (StringUtils.isNotBlank(errorMessage.getHintMsg())) {
                    msg += ":" + errorMessage.getHintMsg();
                }
            } else {
                log.info("::{}::投注-{}订单PA后置检查,检查实时接拒信息接单", orderNo, third);
                ext.setOrderStatus(OrderStatusEnum.ACCEPTED.getCode());
            }
            updateOrder(ext, code, msg, mtsIsCache);
            confirmPoolExecutor.execute(() -> {
                notifyThirdUpdateOrder(ext);
            });
        }
    }

    @Override
    public void orderByCache(ThirdOrderExt ext) {
        if (ext == null || CollectionUtils.isEmpty(ext.getList())) {
            log.error("注单列表extendBean不能为空");
            throw new RcsServiceException("缓存接单,注单信息为空");
        }
        String orderNo = ext.getOrderNo();
        String third = ext.getThird();
        log.info("::{}::投注-{}订单执行缓存接单开始...", orderNo, third);
        String orderInfoStatus = ACCEPTED;
        Integer infoStatus = OrderInfoStatusEnum.MTS_PASS.getCode();
        String infoMsg = third + "缓存接单成功";
        ErrorMessagePrompt errorMessagePrompt = new ErrorMessagePrompt();
        //检查赛事与盘口状态
        if (orderAcceptService.matchAndMarketCheck(orderInfoStatus, orderNo, errorMessagePrompt, third)) {
            orderInfoStatus = REJECTED;
            infoMsg = third + "拒单:" + errorMessagePrompt.getHintMsg();
            log.info("::{}::投注-{}订单执行缓存接单情况:检查赛事与盘口状态拒单:{}", orderNo, third, errorMessagePrompt.getHintMsg());
        }
        //接单前，检查一下注单是否被取消
        if (orderInfoStatus.equals(ACCEPTED)) {
            if (ThirdStrategyFactory.getThirdStrategy(third).orderIsCanceled(orderNo)) {
                log.info("::{}::投注-{}订单执行缓存接单情况:发现订单存在之前手工取消订单操作,不再处理", orderNo, third);
                return;
            }
            infoStatus = OrderInfoStatusEnum.MTS_REFUSE.getCode();
        }
        Integer thirdOrderStatus = ACCEPTED.equals(orderInfoStatus) ? OrderStatusEnum.ACCEPTED.getCode() : OrderStatusEnum.REJECTED.getCode();
        log.info("::{}::投注-{}订单执行缓存接单情况:接拒状态:{}", orderNo, third, orderInfoStatus);
        ext.setThirdOrderStatus(thirdOrderStatus);
        ext.setOrderStatus(thirdOrderStatus);
        //更新状态
        int mtsIsCache = getMtsIsCache(third, 2);
        updateOrder(ext, infoStatus, infoMsg, mtsIsCache);
        log.info("::{}::投注-{}订单执行缓存接单结束...", orderNo, third);
    }

    @Override
    public void orderByThird(ThirdOrderExt ext) {
        MDC.put(LINKID, ext.getLinkId());
        ExtendBean extendBean = ext.getList().get(0);
        String orderNo = extendBean.getOrderId();
        String third = ext.getThird();
        log.info("::{}::投注-{}订单执行第三方接单流程::", orderNo, third);
        ThirdResultVo thirdResultVo;
        try {
            thirdResultVo = ThirdStrategyFactory.getThirdStrategy(third).placeBet(ext);
            log.info("::{}::投注-{}订单执行第三方接单返回:{}", orderNo, third, JSONObject.toJSONString(thirdResultVo));
            if (Objects.isNull(thirdResultVo)) {
                log.warn("::{}::投注-{}订单执行第三方接单返回null,跳过:", orderNo, third);
                return;
            }
            //验证bug45185，设置了延迟单自动进行设置撤销缓存，用来验证到了延迟时间是否进行订单确认
//            if (thirdResultVo.getDelay() > 0) {
//                log.info("::{}::{}::延迟订单，模拟撤单后不可以再确认订单的流程来检验是否ok", orderNo, third);
//                //模拟撤单操作
//                simulatedCancelOrder(orderNo);
//            }
        } catch (RcsServiceException e) {
            log.warn("::{}::投注-{}订单执行第三方接单异常:", orderNo, third, e);
            return;
        }
        //三方订单状态
        Integer thirdOrderStatus = thirdResultVo.getThirdOrderStatus();
        //三方延迟时间
        Integer thirdDelayTime = thirdResultVo.getDelay();
        Integer infoStatus = OrderInfoStatusEnum.MTS_PASS.getCode();
        String thirdNo = thirdResultVo.getThirdNo();
        ext.setThirdOrderNo(thirdNo);
        ext.setThirdDelay(thirdDelayTime);
        ext.setThirdOrderStatus(thirdOrderStatus);
        ext.setThirdResJson(thirdResultVo.getThirdRes());
        //第三方拒单 直接拒单
        int mtsIsCache = getMtsIsCache(third, 3);
        if (thirdOrderStatus == OrderStatusEnum.REJECTED.getCode()) {
            log.info("::{}::投注-数据商{}拒单", orderNo, third);
            infoStatus = OrderInfoStatusEnum.MTS_REFUSE.getCode();
            ext.setOrderStatus(OrderStatusEnum.REJECTED.getCode());
            updateOrder(ext, infoStatus, third + "拒单:" + thirdResultVo.getReasonMsg(), mtsIsCache);
            doCache(ext);
            /*confirmPoolExecutor.execute(() -> {
                notifyThirdUpdateOrder(ext);
            });*/
            return;
        }
        //第三方接单 或（接拒中但是延时时间<0）
        if (thirdOrderStatus == OrderStatusEnum.ACCEPTED.getCode() || (thirdOrderStatus == OrderStatusEnum.WAITING.getCode() && thirdDelayTime != null && thirdDelayTime <= 0)) {
            log.info("::{}::投注-数据商{}接单", orderNo, third);
            ext.setOrderStatus(OrderStatusEnum.ACCEPTED.getCode());
            updateOrder(ext, infoStatus, third + "接单", mtsIsCache);
            //设置缓存
            doCache(ext);
            confirmPoolExecutor.execute(() -> {
                notifyThirdUpdateOrder(ext);
            });
            return;
        }
        if (third.equals(OrderTypeEnum.REDCAT.getPlatFrom())) {
            log.info("::{}::投注-数据商{},由数据商推送mq来处理", orderNo, third);
            return;
        }
        //延迟接单
//        ext.setThirdDelay(thirdResultVo.getDelay());
        ext.setThirdDelay(10);
        ext.setThirdResJson(thirdResultVo.getThirdRes());
        ext.setIsBetAllowed(thirdResultVo.getIsBetAllowed());
        addDelayOrder(ext);
    }

    /**
     * 模拟撤单
     * @param orderNo
     */
    private void simulatedCancelOrder(String orderNo) {
        OrderBean orderBean = new OrderBean();
        orderBean.setCreateTime(1696044315341L);
        orderBean.setCurrencyCode("CNY");
        orderBean.setDeviceType(2);
        orderBean.setHandleStatus(0);
        orderBean.setInfoStatus(5);
        orderBean.setIp("172.21.165.104");
        orderBean.setIpArea("局域网,局域网,");
        orderBean.setIsUpdateOdds(true);
        orderBean.setLimitType(1);
        orderBean.setModifyTime(1696044315381L);
        orderBean.setOrderAmountTotal(10000L);
        orderBean.setOrderNo(orderNo);
        orderBean.setOrderStatus(2);
        orderBean.setProductAmountTotal(10000L);
        orderBean.setProductCount(1L);
        orderBean.setReason("验证延迟确认订单撤销后不需要再次跟数据商确认订单");
        orderBean.setSeriesType(1);
        orderBean.setSportId(3);
        orderBean.setTenantId(2L);
        orderBean.setUid(508450770767400018L);
        orderBean.setUserTagLevel(0);
        orderBean.setValidateResult(2);
        orderBean.setVipLevel(0);
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setBetAmount(10000L);
        item.setBetNo("508813294602604");
        item.setBetTime(1696044315341L);
        item.setCreateTime(1696044315341L);
        item.setHandleStatus(0);
        item.setId(2086951L);
        item.setIsRelationScore(0);
        item.setMarketId(145254325834659515L);
        item.setMarketType("EU");
        item.setMarketValue("8.5");
        item.setMatchId(3540880L);
        item.setMatchInfo("旧金山巨人队 v 洛杉矶道奇");
        item.setMatchProcessId(411L);
        item.setMatchType(2);
        item.setMaxWinAmount(8400.0d);
        item.setModifyTime(1696044315341L);
        item.setOddFinally("1.84");
        item.setOddsValue(184000.0d);
        item.setOrderNo(orderNo);
        item.setOrderStatus(2);
        item.setPlaceNum(1);
        item.setPlayId(244);
        item.setPlayName("全场大小");
        item.setPlayOptions("Over");
        item.setPlayOptionsId(143118039309354939L);
        item.setPlayOptionsName("大8.5");
        item.setRiskChannel(2);
        item.setScoreBenchmark("0.0");
        item.setSeriesType(1);
        item.setSportId(3);
        item.setSportName("棒球");
        item.setSubPlayId("244");
        item.setTournamentId(821289L);
        item.setUid(508450770767400018L);
        item.setValidateResult(1);
        item.setVolumePercentage(new BigDecimal("1.00"));
        items.add(item);
        orderBean.setItems(items);
        //发送撤单mq
        producerSendMessageUtils.sendMessage("queue_reject_mts_order,rejectOrder", orderBean);
    }

    @Override
    public void checkOrderAfter(ThirdResultVo thirdResultVo, ThirdOrderExt ext) {
        String orderNo = ext.getOrderNo();
        String third = ext.getThird();
        //三方订单状态
        Integer thirdOrderStatus = thirdResultVo.getThirdOrderStatus();
        //三方延迟时间
        Integer thirdDelayTime = thirdResultVo.getDelay();
        Integer infoStatus = OrderInfoStatusEnum.MTS_PASS.getCode();
        String thirdNo = thirdResultVo.getThirdNo();
        ext.setThirdOrderNo(thirdNo);
        ext.setThirdDelay(thirdDelayTime);
        ext.setThirdOrderStatus(thirdOrderStatus);
        ext.setThirdResJson(thirdResultVo.getThirdRes());
        String reasonMsg = thirdResultVo.getReasonMsg();
        //第三方拒单 直接拒单
        int mtsIsCache = getMtsIsCache(third, 3);
        if (thirdOrderStatus == OrderStatusEnum.REJECTED.getCode()) {
            log.info("::{}::投注-数据商{}拒单", orderNo, third);
            infoStatus = OrderInfoStatusEnum.MTS_REFUSE.getCode();
            ext.setOrderStatus(OrderStatusEnum.REJECTED.getCode());
            String infoMsg = StringUtils.isNotBlank(reasonMsg) ? reasonMsg : third + "拒单";
            updateOrder(ext, infoStatus, infoMsg, mtsIsCache);
            //notifyThirdUpdateOrder(orderNo, thirdResultVo.getThirdNo(), thirdOrderStatus, third);
            doCache(ext);
     /*       confirmPoolExecutor.execute(()->{
                notifyThirdUpdateOrder(ext);
            });*/
            return;
        }
        //第三方接单 或（接拒中但是延时时间<0）
        if (thirdOrderStatus == OrderStatusEnum.ACCEPTED.getCode() || (thirdOrderStatus == OrderStatusEnum.WAITING.getCode() && thirdDelayTime != null && thirdDelayTime <= 0)) {
            log.info("::{}::投注-数据商{}接单", orderNo, third);
            ext.setOrderStatus(OrderStatusEnum.ACCEPTED.getCode());
            updateOrder(ext, infoStatus, third + "接单", mtsIsCache);
            //设置缓存
            doCache(ext);
            confirmPoolExecutor.execute(() -> {
                notifyThirdUpdateOrder(ext);
            });
            return;
        }
        if (third.equals(OrderTypeEnum.REDCAT.getPlatFrom())) {
            log.info("::{}::投注-数据商{},由数据商推送mq来处理", orderNo, third);
            return;
        }
        //延迟接单
        ext.setThirdDelay(thirdResultVo.getDelay());
        ext.setThirdResJson(thirdResultVo.getThirdRes());
        addDelayOrder(ext);
        MDC.remove(LINKID);
    }

    @Override
    public void updateOrder(ThirdOrderExt ext, Integer infoStatus, String infoMsg, Integer mtsIsCache) {
        Stopwatch stopwatch=Stopwatch.createStarted();
        //1.通知业务更新订单状态
        String orderNo = ext.getOrderNo();
        Integer orderStatus = ext.getOrderStatus();
        String third = ext.getThird();
        Map<String, Object> map = new HashMap<>();
        map.put("orderNo", orderNo);
        map.put("status", orderStatus);
        map.put("isOddsChange", false);
        map.put("infoCode", 0);
        map.put("infoStatus", infoStatus);
        map.put("infoMsg", infoMsg);
        //第三方秒接的 排除掉接口响应的影响 直接默认为秒接
        if (ext.getThirdOrderStatus() == OrderStatusEnum.ACCEPTED.getCode()) {
            /*            map.put("infoStatus", 12); //一键秒接*/
            map.put("handleTime", ext.getList().get(0).getItemBean().getCreateTime());
        } else {
            map.put("handleTime", System.currentTimeMillis());
        }
        map.put("currentEvent", "");
        map.put("mtsIsCache", mtsIsCache);

        Map<String, String> oddsChangeList = new HashMap<>();
        List<TOrderDetail> orderDetailList = null;
        try {
            LambdaQueryWrapper<TOrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
            orderDetailWrapper.eq(TOrderDetail::getOrderNo, orderNo);
            orderDetailList = orderDetailMapper.selectList(orderDetailWrapper);
            log.info("::{}::后置检查，查询订单明细完成，耗时:{}",orderNo,stopwatch.elapsed(TimeUnit.MILLISECONDS));
            if (!CollectionUtils.isEmpty(orderDetailList)) {
                oddsChangeList = orderAcceptService.queryOddsRange(orderDetailList, third);
            }
        } catch (Exception e) {
            log.error("::{}::投注-{}订单获取详情异常:", orderNo, third, e);
        }
        if (oddsChangeList.size() > 0) {
            map.put("isOddsChange", true);
            //map.put("oddsChangeList", oddsChangeList);
            map.put("oddsRange", oddsChangeList);
        }
        String userGroup = "";
        if (ext.getOrderGroup() != null) {
            userGroup = "_" + ext.getOrderGroup();
        }
        String topic = RCS_BUS_THIRD_ORDER_STATUS + userGroup;
        String tag = third + "Order";
        sendMessage.sendMessage(topic + "," + tag + "," + orderNo, map);
        log.info("::{}::投注-{}订单更新推送业务TOPIC=[" + topic + "]完成,下发数据={}", orderNo, third, JSONObject.toJSONString(map));
        //2.更新第三方表订单
        stopwatch.reset().start();
        ThirdStrategyFactory.getThirdStrategy(third).updateOrder(ext);
        log.info("::{}::后置检查查询接单状态完成，耗时:{}",orderNo,stopwatch.elapsed(TimeUnit.MILLISECONDS));
        //3.通知风控更新订单状态
        if (CollectionUtils.isEmpty(orderDetailList)) {
            log.warn("::{}::投注-{}订单未获取到订单详情,跳过", orderNo, third);
            return;
        }
        TOrder order = null;
        try {
            stopwatch.reset().start();
            QueryWrapper<TOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TOrder::getOrderNo, orderNo);
            order = orderMapper.selectOne(queryWrapper);
            log.info("::{}::后置检查，查询订单一次订单,耗时:{}",orderNo,stopwatch.elapsed(TimeUnit.MILLISECONDS));
            //极端情况会有对象延迟的情况  这种情况极少 此处做兼容
            int times = 0;
            while (order == null && times < 3) {
                ++times;
                Thread.sleep(1000);
                order = orderMapper.selectOne(queryWrapper);
                log.info("::{}::投注-订单重新获取{}次", order.getOrderNo(), times);
            }
            log.info("::{}::后置检查，查询订单次数完成，耗时:{}",orderNo,stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            log.error("::{}::投注-{}订单重新获取异常", orderNo, third, e);
        }
        if (order == null) {
            log.warn("::{}::投注-{}订单入库延迟了未读取到", orderNo, third);
            return;
        }
        OrderBean orderBeanDto = new OrderBean();
        BeanCopyUtils.copyProperties(order, orderBeanDto);
        orderBeanDto.setOrderStatus(orderStatus);
        orderBeanDto.setValidateResult(orderStatus);
        orderBeanDto.setReason(infoMsg);
        orderBeanDto.setInfoStatus(orderStatus == 1 ? OrderInfoStatusEnum.MTS_PASS.getCode() : OrderInfoStatusEnum.MTS_REFUSE.getCode());
        orderBeanDto.setOrderNo(orderNo);
        orderBeanDto.setOddsChangeList(new ArrayList<>());
        orderBeanDto.setSecondaryLabelIdsList(ext.getSecondaryLabelIdsList());
        //构建订单详情信息
        List<OrderItem> OrderItemList = BeanCopyUtils.copyPropertiesList(orderDetailList, OrderItem.class);
        for (OrderItem item : OrderItemList) {
            item.setValidateResult(orderStatus);
            item.setOrderStatus(orderStatus);
            item.setModifyTime(System.currentTimeMillis());

            // bug：44661 （只有三方订单存在该情况）
            // 这里由于订单状态不为待处理时oddsValue的值是计算过后的值（小数）而参数handleAfterOddsValue截取oddsValue两位小数，导致在数据下游里处理最后取值为0
            // 故而为了稳妥不修改代码其他逻辑,在这里设置oddsValue值时做一个兜底操作
            item.setOddsValue(new BigDecimal(String.valueOf(item.getOddsValue())).multiply(new BigDecimal("100000")).doubleValue());
        }
        orderBeanDto.setItems(OrderItemList);
        tag = third + "_notify_update_order";
        sendMessage.sendMessage(MqConstants.RCS_ORDER_UPDATE + "," + tag + "," + orderNo, orderBeanDto);
        log.info("::{}::投注-{}订单更新推送风控TOPIC=[queue_update_order]完成,下发数据={}", orderNo, third, JSONObject.toJSONString(orderBeanDto));

    }

    @Override
    public void notifyThirdUpdateOrder(ThirdOrderExt ext) {
        ThirdStrategyFactory.getThirdStrategy(ext.getThird()).orderConfirm(ext);
    }

    /**
     * 判断是否缓存概率性接单
     *
     * @param ext sdk订单传参
     */
    @Override
    public boolean orderIsCache(ThirdOrderExt ext) {
        if (ext == null || CollectionUtils.isEmpty(ext.getList())) {
            log.error("注单列表extendBean不能为空");
            return false;
        }
        ExtendBean extendBean = ext.getList().get(0);
        String orderNo = extendBean.getOrderId();
        String third = ext.getThird();
        //串关 不走缓存
        if (ext.getList().size() > 1) {
            log.info("::{}::{}订单-串关不走缓存模式", orderNo, third);
            return false;
        }
        try {
            //标签延迟的 不走缓存
            LambdaQueryWrapper<RcsLabelLimitConfig> limitConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
            limitConfigLambdaQueryWrapper.eq(RcsLabelLimitConfig::getTagId, extendBean.getUserTagLevel());
            List<RcsLabelLimitConfig> delayList = labelLimitConfigMapper.selectList(limitConfigLambdaQueryWrapper);
            if (ObjectUtils.isNotEmpty(delayList) && ObjectUtils.isNotEmpty(delayList.get(0).getBetExtraDelay()) && delayList.get(0).getBetExtraDelay() > 0) {
                Integer delayTime = delayList.get(0).getBetExtraDelay();
                if (null != delayTime) {
                    log.info("::{}::投注-{}订单缓存判断,有标签延迟配置不走缓存接单,标签:{},延期时间:{}", orderNo, third, extendBean.getUserTagLevel(), delayTime);
                    return false;
                }
            }
            OrderItem orderItem = extendBean.getItemBean();
            Long optionId = orderItem.getPlayOptionsId();
            String oddFinally = orderItem.getOddFinally();
            String thirdOrderCache = String.format(THIRD_ORDER_CACHE, third, optionId, oddFinally, ext.getAcceptOdds());
            thirdOrderCache = redisClient.get(thirdOrderCache);
            if (StringUtils.isBlank(thirdOrderCache)) {
                log.info("::{}::投注-{}订单缓存判断,无缓存跳过", orderNo, third);
                return false;
            }
            Random rd = new Random();
            int num = rd.nextInt(100);
            //接单概率
            String orderRate = cacheConfig.getRate();
            if (num > Integer.parseInt(orderRate)) {
                log.info("::{}::投注-{}订单缓存判断流程跳过,随机未命中:orderRate={},随机值={}", orderNo, third, orderRate, num);
                return false;
            }
            log.info("::{}::投注-{}订单缓存判断通过:orderRate={},随机值={}", orderNo, third, orderRate, num);
            return true;
        } catch (Exception e) {
            log.info("::{}::投注-{}订单缓存判断出现异常:", orderNo, third, e);
            return false;
        }
    }


    /**
     * 订单缓存处理
     *
     * @param ext 订单对象
     */
    private void doCache(ThirdOrderExt ext) {
        if (ext == null || ext.getList() == null || ext.getList().size() != 1) {
            return;
        }
        ExtendBean detail = ext.getList().get(0);
        String orderNo = detail.getOrderId();
        String optionId = detail.getSelectId();
        String oddFinally = detail.getOdds();
        String third = ext.getThird();
        int acceptOdds = ext.getAcceptOdds();
        String thirdOrderCache = String.format(THIRD_ORDER_CACHE, third, optionId, oddFinally, acceptOdds);
        try {
            if (ext.getThirdOrderStatus() == OrderStatusEnum.ACCEPTED.getCode()) {
                String orderCacheTime = cacheConfig.getTime();
                redisClient.setExpiry(thirdOrderCache, "1", Long.valueOf(orderCacheTime));
                log.info("::{}::投注-{}订单接拒状态临时缓存完成,key={},缓存时间={}s", orderNo, third, thirdOrderCache, orderCacheTime);
            } else if (ext.getThirdOrderStatus() == OrderStatusEnum.REJECTED.getCode()) {
                redisClient.delete(thirdOrderCache);
                log.info("::{}::投注-{}订单接拒状态删除缓存完成,key={}", orderNo, third, thirdOrderCache);
            }
        } catch (Exception e) {
            log.error("::{}::投注-{}订单接拒状态缓存处理异常,key={}", orderNo, third, thirdOrderCache);
        }
    }


    /**
     * 添加到接拒队列
     */
    @Override
    public void addDelayOrder(ThirdOrderExt ext) {
        String third = ext.getThird();
        if (CollectionUtils.isEmpty(ext.getList())) {
            log.warn("::{}::投注-{}订单添加到接拒队列,订单信息为空不处理", ext.getOrderNo(), third);
            return;
        }
        //isBetAllowed如果为false，数据商会从ably发送拒单数据，需要根据拒单数据进行拒单,所以根据orderNo存入缓存
        if (!ext.getIsBetAllowed()) {
            log.info("Ably::{}::注单延迟返回的isBetAllowed如果为false，将订单存入缓存", ext.getThirdOrderNo());
            String isNotAllowedBetKey = String.format(GTS_IS_NOT_ALLOWED_ORDER,ext.getThirdOrderNo());
            Long expiry = ext.getThirdDelay().longValue() + 1L;
            redisClient.setExpiry(isNotAllowedBetKey, ext, expiry);
        }
        String orderId = ext.getList().get(0).getOrderId();
        try {
            ThirdOrderDelayVo delayVo = new ThirdOrderDelayVo();
            delayVo.setOrderNo(orderId);
            delayVo.setAcceptTime(System.currentTimeMillis());
            delayVo.setThird(ext.getThird());
            delayVo.setTotalMoney(ext.getPaTotalAmount());
            delayVo.setDelayTime(ext.getThirdDelay());
            delayVo.setThirdOrderNo(ext.getThirdOrderNo());
            delayVo.setList(ext.getList());
            delayVo.setOrderGroup(ext.getOrderGroup());
            delayVo.setSeriesType(ext.getSeriesType());
            //推送到接拒队列
            sendMessage.sendMessage(RCS_RISK_THIRD_ORDER_REJECT, third + "_ORDER_DELAY", orderId, delayVo);
        } catch (Exception e) {
            log.error("::{}::投注-{}订单添加到接拒队列异常:", orderId, third, e);
        }
    }

    /**
     * 通知业务状态
     *
     * @param third 第三方标识
     * @param num   策略标识
     */
    @Override
    public int getMtsIsCache(String third, int num) {
        int isCache = OrderTypeEnum.MTS.getValue();
        if (OrderTypeEnum.BTS.getPlatFrom().equalsIgnoreCase(third)) {
            switch (num) {
                case 1:
                    isCache = MtsIsCacheEnum.BTS_PA.getValue();
                    break;
                case 2:
                    isCache = MtsIsCacheEnum.BTS_CACHE.getValue();
                    break;
                default:
                    isCache = MtsIsCacheEnum.BTS.getValue();
            }
        }
        if (OrderTypeEnum.GTS.getPlatFrom().equalsIgnoreCase(third)) {
            switch (num) {
                case 1:
                    isCache = MtsIsCacheEnum.GTS_PA.getValue();
                    break;
                case 2:
                    isCache = MtsIsCacheEnum.GTS_CACHE.getValue();
                    break;
                default:
                    isCache = MtsIsCacheEnum.GTS.getValue();
            }
        }
        if (OrderTypeEnum.CTS.getPlatFrom().equalsIgnoreCase(third)) {
            switch (num) {
                case 1:
                    isCache = MtsIsCacheEnum.CTS_PA.getValue();
                    break;
                case 2:
                    isCache = MtsIsCacheEnum.CTS_CACHE.getValue();
                    break;
                default:
                    isCache = MtsIsCacheEnum.CTS.getValue();
            }
        }
        if (OrderTypeEnum.REDCAT.getPlatFrom().equalsIgnoreCase(third)) {
            switch (num) {
                case 1:
                    isCache = MtsIsCacheEnum.REDCAT_PA.getValue();
                    break;
                case 2:
                    isCache = MtsIsCacheEnum.REDCAT_CACHE.getValue();
                    break;
                default:
                    isCache = MtsIsCacheEnum.REDCAT.getValue();
            }
        }


        return isCache;
    }

}
