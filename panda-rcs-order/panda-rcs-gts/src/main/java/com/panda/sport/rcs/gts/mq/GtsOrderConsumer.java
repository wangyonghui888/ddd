package com.panda.sport.rcs.gts.mq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.gts.common.Constants;
import com.panda.sport.rcs.gts.gtsenum.SportIdEnum;
import com.panda.sport.rcs.gts.service.GtsCommonService;
import com.panda.sport.rcs.gts.service.GtsThirdApiService;
import com.panda.sport.rcs.gts.service.RcsGtsOrderExtService;
import com.panda.sport.rcs.gts.task.GtsOrderDelayTask;
import com.panda.sport.rcs.gts.util.CopyUtils;
import com.panda.sport.rcs.gts.util.SystemThreadLocal;
import com.panda.sport.rcs.gts.vo.GtsBetResultVo;
import com.panda.sport.rcs.gts.vo.GtsExtendBean;
import com.panda.sport.rcs.gts.vo.GtsMerchantOrder;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.mapper.limit.RcsLabelLimitConfigMapper;
import com.panda.sport.rcs.pojo.RcsGtsOrderExt;
import com.panda.sport.rcs.pojo.RcsLabelLimitConfig;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.panda.sport.rcs.gts.common.Constants.*;

/**
 * GTS订单处理
 * @author lithan
 * @date 2023-01-07 13:28:02
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = RCS_RIKS_GTS_ORDER,
        consumerGroup = "rcs_riks_gts_order_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class GtsOrderConsumer implements RocketMQListener<JSONObject>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Resource
    RcsGtsOrderExtService gtsOrderExtService;

    @Resource
    GtsCommonService gtsCommonService;

    @Resource
    RedisClient redisClient;

    @Autowired
    TUserMapper userMapper;
    @Resource
    private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;

    @Resource
    RcsLabelLimitConfigMapper labelLimitConfigMapper;

    @Resource
    private TOrderDetailMapper orderDetailMapper;
    @Resource
    GtsThirdApiService gtsThirdApiService;

    @Override
    public void onMessage(JSONObject dataMap) {
        String orderId = "";
        try {
            //串关信息
            String seriesType = dataMap.get("seriesNum").toString();
            //总投注金额
            String totalMoney = ObjectUtils.isEmpty(dataMap.get("totalMoney")) ? "0" : String.valueOf(dataMap.get("totalMoney"));
            //是否自动接受赔率变化 1：自动接收更好的赔率 2：自动接受任何赔率变动 3：不自动接受赔率变动
            String acceptOdds = ObjectUtils.isEmpty(dataMap.get("acceptOdds")) ? null : String.valueOf(dataMap.get("acceptOdds"));
            //注单列表
            List<ExtendBean> list = JSONObject.parseObject(JSONObject.toJSONString(dataMap.get("list")), new TypeReference<List<ExtendBean>>() {});
            //列表扩展对象
            List<GtsExtendBean> gtsExtendBeanList = CopyUtils.clone(list, GtsExtendBean.class);
            //订单号
            orderId = list.get(0).getItemBean().getOrderNo();
            //线程记录订单号 方便日志用
            SystemThreadLocal.set("orderNo", orderId);
            log.info("::{}::gts收到：{}", SystemThreadLocal.get().get("orderNo"), JSONObject.toJSON(list));
            //一个订单只处理一次
            if (StringUtils.isNotBlank(redisClient.get(String.format(GTS_ORDER_OPSTATUS, orderId)))) {
                log.info("::{}::订单gts已处理,跳过：{}", orderId);
                return;
            }
            //记录订单已处理过
            redisClient.setExpiry(String.format(GTS_ORDER_OPSTATUS, orderId), acceptOdds, 5 * 60L);
            //记录订单的接受赔率模式
            redisClient.setExpiry(String.format(GTS_ORDER_ODDSCHANGETYPE, orderId), acceptOdds, 20L);

            //处理订单 分为三种模式   缓存接单/商户内部接单/第三方gts接单
            if (!CollectionUtils.isEmpty(list)) {
                //填充基础信息 查询第三方原始数据
                gtsCommonService.convertAllParam(gtsExtendBeanList);
                //保存gts订单信息
                saveGtsOrder(orderId, totalMoney, String.valueOf(list.get(0).getMtsAmount()), "");
                //是否走缓存
                boolean flag = doCache(list, acceptOdds);

                //按照商户级别控制是否某个商户的注单都不提交GTS  1不走GTS
                String merchantGtsStatusList = redisClient.get(GTS_MERCHANT_SENDTICKET_STATUS);
                if (StringUtils.isBlank(merchantGtsStatusList)) {
                    merchantGtsStatusList = "[]";
                }
                JSONArray jsonArray = JSONArray.parseArray(merchantGtsStatusList);
                String merchantCode = userMapper.selectByUserId(Long.valueOf(list.get(0).getUserId())).getMerchantCode();
                //是否特殊商户不走gts 走内部接单
                boolean isGtsPa = jsonArray.contains(merchantCode);
                log.info("::{}::{}:{}商户配置是否走gts:{}", orderId, merchantCode, isGtsPa, merchantGtsStatusList);
                //足蓝不走
                if (list.get(0).getSportId().equals("1") || list.get(0).getSportId().equals("2")) {
                    isGtsPa = false;
                    log.info("::{}::足蓝不走-商户配置是否走gts:球种:{}", orderId, list.get(0).getSportId());
                }
                //内部接单
                if (isGtsPa && list.size() == 1) {
                    //早盘滚球
                    int matchTYpe = list.get(0).getItemBean().getMatchType();
                    //添加到延迟处理订单
                    addDelayOrder(orderId, matchTYpe, list);
                } else if (flag) {
                    log.info("::{}::gts订单走缓存", orderId);
                    Long ticketId = redisClient.incrBy(GTS_AUTO_TICKETID, 1) * (-1);
                    String status = ACCEPTED;
                    String orderNo = orderId;
                    String jsonValue = "{}";
                    Integer reasonCode = -100;
                    String reasonMsg = "缓存接单成功";
                    Integer isCache = 0;
                    gtsCommonService.updateGtsOrder(ticketId.toString(), status, orderNo, jsonValue, reasonCode, reasonMsg, isCache);
                    log.info("::{}::gts订单走缓存完成:{}", orderId);
                } else {
                    //订单走第三方  往gts请求
                    GtsBetResultVo gtsVo = gtsThirdApiService.gtsAssessmentBet(gtsExtendBeanList, Long.valueOf(totalMoney), Integer.valueOf(seriesType));
                    log.info("::{}::gts订单结果:{}", orderId, JSONObject.toJSONString(gtsVo));
                    String isScroll = gtsExtendBeanList.get(0).getIsScroll();
                    //是否中场休息
                    boolean intermission = getMatchPeriod(list.get(0).getItemBean());
                    if (!intermission && isScroll.equals("1") && gtsVo.getDelayTime() != null && gtsVo.getDelayTime() > 0) {
                        log.info("::{}::gts订单第一步成功有延迟:{}:早盘滚球{}:intermission:{}", orderId, gtsVo.getDelayTime(), isScroll,intermission);
                        //gts返回需要延迟接单 添加到延迟处理订单
                        addDelayOrder(orderId, gtsVo.getDelayTime().toString(), gtsExtendBeanList, Integer.valueOf(seriesType), Long.valueOf(totalMoney));
                        //单独更新gts表
                        LambdaQueryWrapper<RcsGtsOrderExt> gtsOrderExtLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        gtsOrderExtLambdaQueryWrapper.eq(RcsGtsOrderExt::getOrderNo, orderId);
                        RcsGtsOrderExt gtsOrderExt = gtsOrderExtService.getOne(gtsOrderExtLambdaQueryWrapper);
                        gtsOrderExt.setResult(gtsVo.getThridValue());
                        gtsOrderExt.setUpdateTime(new Date());
                        gtsOrderExtService.updateById(gtsOrderExt);
                    } else if (gtsVo.getStatus().equals(ACCEPTED)) {
                        log.info("::{}::gts订单第一步成功 不走延迟:isScroll{}:intermission:{}", orderId, isScroll, intermission);
                        //如果接单
                        gtsCommonService.updateGtsOrder(gtsVo.getTickeId(), gtsVo.getStatus(), orderId, gtsVo.getThridValue(), 1, gtsVo.getMessage(), 1);
                        //通知第三方确认接单
                        gtsThirdApiService.gtsReceiveBet(gtsExtendBeanList, Long.valueOf(totalMoney), Integer.valueOf(seriesType), ACCEPTED);
                    } else if (gtsVo.getStatus().equals(REJECTED)) {
                        log.info("::{}::gts订单第一步拒单无延迟", orderId);
                        //如果拒单
                        gtsCommonService.updateGtsOrder(gtsVo.getTickeId(), gtsVo.getStatus(), orderId, gtsVo.getThridValue(), 0, gtsVo.getMessage(), 0);
                        //通知第三方确认拒单
                        gtsThirdApiService.gtsReceiveBet(gtsExtendBeanList, Long.valueOf(totalMoney), Integer.valueOf(seriesType), REJECTED);
                    }
                }
            }
        } catch (Exception e) {
            log.info("::{}::GTS订单异常--{},{}", orderId, e.getMessage(), e);
            return;
        } finally {
            SystemThreadLocal.remove();
        }
        return;
    }

    /**
     * 延迟接单 订单添加
     *
     * @param orderId   订单号
     * @param matchTYpe 早盘 滚球
     * @param list      投注list
     */
    private void addDelayOrder(String orderId, Integer matchTYpe, List<ExtendBean> list) {
        GtsMerchantOrder merchantOrder = new GtsMerchantOrder();
        merchantOrder.setOrderNo(orderId);
        merchantOrder.setOrderTime(System.currentTimeMillis() + "");
        List<TOrderDetail> tOrderDetailList = orderDetailMapper.queryOrderDetails(orderId);
        merchantOrder.setTOrderDetailList(tOrderDetailList);
        String orderDelayTime = redisClient.hGet(String.format("rcs:tournament:property:%s", String.valueOf(list.get(0).getTournamentId())), "orderDelayTime");
        //String orderDelayTime = gtsCommonService.getTournamentTemplateValue(list.get(0).getTournamentId(), "2");
        log.info("::{}::赛事:{}缓存等待时间={}", orderId, list.get(0).getItemBean().getMatchId(), orderDelayTime);
        //获取联赛设置默认等待时间
        RcsMatchMarketConfig config = rcsMatchMarketConfigMapper.queryMarketConfig(list.get(0).getItemBean());
        if (config != null) {
            orderDelayTime = config.getWaitSeconds().toString();
            log.info("::{}::赛事:{}优先取的等待时间={}", orderId, list.get(0).getItemBean().getMatchId(), orderDelayTime);
        }
        if (StringUtils.isBlank(orderDelayTime)) {
            orderDelayTime = "5";
        }
        if (matchTYpe == 1) {
            orderDelayTime = "0";
        }
        log.info("::{}::赛事:{}确认等待时间={}", orderId, list.get(0).getItemBean().getMatchId(), orderDelayTime);
        merchantOrder.setDelayTime(orderDelayTime);
        GtsOrderDelayTask.delayMap.put(orderId, JSONObject.toJSONString(merchantOrder));
        log.info("::{}::gts订单商户不提交gts:加入自动检测：{}", orderId, JSONObject.toJSONString(merchantOrder));
    }

    private void addDelayOrder(String orderId, String gtsDelay,List<GtsExtendBean> gtsExtendBeanList,Integer seriesType,Long totalMoney) {
        GtsMerchantOrder merchantOrder = new GtsMerchantOrder();
        merchantOrder.setOrderNo(orderId);
        merchantOrder.setOrderTime(System.currentTimeMillis() + "");
        List<TOrderDetail> tOrderDetailList = getOrderDetail(orderId);
        merchantOrder.setTOrderDetailList(tOrderDetailList);
        merchantOrder.setDelayTime(gtsDelay);
        //gts相关  因为延迟接单后 还需要和gts
        merchantOrder.setIsGts(1);
        merchantOrder.setGtsExtendBeanList(gtsExtendBeanList);
        merchantOrder.setSeriesType(seriesType);
        merchantOrder.setTotalMoney(totalMoney);
        GtsOrderDelayTask.delayMap.put(orderId, JSONObject.toJSONString(merchantOrder));
        log.info("::{}::gts订单加入自动检测：{}", orderId, JSONObject.toJSONString(merchantOrder));
    }

    private boolean doCache(List<ExtendBean> list, String oddsChangeType) {
        OrderItem orderItem = list.get(0).getItemBean();
        String orderNo = orderItem.getOrderNo();
        if (list.size() > 1) {
            log.info("::{}::gts订单缓存流程跳过:非单关", orderNo);
            return false;
        }
        //标签延迟的 也不走缓存
        LambdaQueryWrapper<RcsLabelLimitConfig> limitConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        limitConfigLambdaQueryWrapper.eq(RcsLabelLimitConfig::getTagId, list.get(0).getUserTagLevel());
        List<RcsLabelLimitConfig> delayList = labelLimitConfigMapper.selectList(limitConfigLambdaQueryWrapper);
        if (ObjectUtils.isNotEmpty(delayList) && ObjectUtils.isNotEmpty(delayList.get(0).getBetExtraDelay()) && delayList.get(0).getBetExtraDelay() > 0) {
            Integer delayTime = delayList.get(0).getBetExtraDelay();
            log.info("::{}::gts订单缓存流程:标签{}延期时间{}", orderNo, list.get(0).getUserTagLevel(), delayTime);
            if (null != delayTime) {
                return false;
            }
        }

        Long optionId = orderItem.getPlayOptionsId();
        String oddFinally = orderItem.getOddFinally();
        String gtsOrderCache = String.format(Constants.GTS_ORDER_CACHE, optionId, oddFinally, oddsChangeType);
        gtsOrderCache = redisClient.get(gtsOrderCache);
        if (StringUtils.isBlank(gtsOrderCache)) {
            log.info("::{}::gts订单缓存流程跳过:无缓存", orderNo);
            return false;
        }
        //缓存存在的情况  概率性接单
        String gtsOrderRate = RcsLocalCacheUtils.getValue(GTS_ORDER_RATE, redisClient::get, 5 * 60 * 1000L);
        if (StringUtils.isEmpty(gtsOrderRate)) {
            gtsOrderRate = "70";
        }
        Random rd = new Random();
        int num = rd.nextInt(100);
        if (num > Integer.valueOf(gtsOrderRate)) {
            log.info("::{}::GTS订单缓存流程跳过:随机未命中:gtsOrderRate={}:num={}", orderNo, gtsOrderRate, num);
            return false;
        }
        log.info("::{}::GTS订单缓存流程通过:gtsOrderRate={}:num={}", orderNo, gtsOrderRate, num);
        return true;
    }

    /**
     * 保存订单
     *
     * @param orderNo
     * @param paMount
     * @param gtsAmount
     * @param requestJson
     */
    private void saveGtsOrder(String orderNo, String paMount, String gtsAmount, String requestJson) {
        RcsGtsOrderExt ext = new RcsGtsOrderExt();
        ext.setOrderNo(orderNo);
        ext.setRequestJson(requestJson);
        ext.setStatus("INIT");
        ext.setPaAmount(paMount);
        ext.setGtsAmount(gtsAmount);
        ext.setCreTime(new Date());
        gtsOrderExtService.addGtsOrder(ext);
    }

    /**
     * 查询订单详情
     *
     * @param orderNo
     * @return
     */
    private List<TOrderDetail> getOrderDetail(String orderNo) {
        LambdaQueryWrapper<TOrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TOrderDetail::getOrderNo, orderNo);
        return orderDetailMapper.selectList(lambdaQueryWrapper);
    }

    //获取赛事 阶段 是否中场休息  能否秒接
    private boolean getMatchPeriod(OrderItem orderItem) {
        Long sportId = orderItem.getSportId().longValue();
        String periodRediskey = String.format(RCS_DATA_KEYCACHE_MATCHTEMPINFO, orderItem.getMatchId());
        String period = RcsLocalCacheUtils.getValue(periodRediskey, "period", redisClient::hGet, 10 * 1000L);
        log.info("::{}::赛事秒接缓存periodRediskey:{},value={}", SystemThreadLocal.get().get("orderNo"), periodRediskey, period);

        if (Arrays.asList(1, 2, 3, 5, 7, 8, 9, 10).contains(orderItem.getSportId())) {
            if (StringUtils.isNotBlank(period)) {
                Integer periodId = Integer.parseInt(period);
                if (SportIdEnum.isFootball(sportId) || SportIdEnum.isBasketball(sportId)) {
                    if (Arrays.asList(302, 31).contains(periodId)) {
                        //中场休息秒接
                        log.info("::{}::中场休息秒接", SystemThreadLocal.get().get("orderNo"));
                        return true;
                    }
                } else if (SportIdEnum.isTennis(sportId) || SportIdEnum.isPingPong(sportId) || SportIdEnum.isVolleyBall(sportId) || SportIdEnum.isBadminton(sportId.intValue())) {
                    if (Arrays.asList(301, 302, 303, 304, 305, 306, 800, 900, 1000, 1100, 1200).contains(periodId)) {
                        //中场休息秒接
                        log.info("::{}::中场休息秒接", SystemThreadLocal.get().get("orderNo"));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}