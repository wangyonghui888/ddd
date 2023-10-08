package com.panda.sport.sdk.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.sdk.annotation.AutoInitMethod;
import com.panda.sport.sdk.constant.BaseConstants;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.constant.RedisKeys;
import com.panda.sport.sdk.core.JedisClusterPipeline;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.util.DateUtils;
import com.panda.sport.sdk.vo.LimitDelayVo;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Description : 商户单日限额  和用户单日限额 延迟入redis
 * @Author : lithan
 * @Date : 2022-11-27
 * --------  ---------  --------------------------
 */
@Singleton
@AutoInitMethod(init = "init")
public class LimitDelayService {
    private static final Logger log = LoggerFactory.getLogger(LimitDelayService.class);
    @Inject
    JedisClusterServer jedisClusterServer;
    @Inject
    LimitConfigService limitConfigService;
    @Inject
    private MerchantLimitWarnService merchantLimitWarnService;

    //延迟需要计算的订单 商户单日
    private Map<String, List<LimitDelayVo>> merchantLimitDelayMap = new ConcurrentHashMap();

    //延迟需要计算的订单 用户单日
    private Map<String, List<LimitDelayVo>> userLimitDelayMap = new ConcurrentHashMap();

    /**
     * 商户单日限额 定期入库
     *
     * @param extendBean
     * @param settleItem
     */
    public void merchantLimitDelay(ExtendBean extendBean, SettleItem settleItem) {

        try {
            /**
             *原商户限额逻辑 保留
             */
            String dateExpect = DateUtils.getDateExpect(settleItem.getSettleTime());
            Long currentPaidAmount = limitConfigService.businessLimitIncrBy(settleItem.getSettleTime(), extendBean.getBusId(), extendBean.getProfit());
            log.info("::结算派彩处理::-商户单日定期更新单日:{}本次金额:{}:最终金额:{}", extendBean.getBusId(), extendBean.getProfit(), currentPaidAmount);
            RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo = limitConfigService.getBusinessLimit(Long.valueOf(extendBean.getBusId()));
            Long businessLimit = rcsQuotaBusinessLimitResVo.getBusinessSingleDayLimit();
            log.info("::结算派彩处理::-商户单日最大赔付判断{},{},{},{}", extendBean.getBusId(), currentPaidAmount, businessLimit, currentPaidAmount >= businessLimit);
            String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, dateExpect, extendBean.getBusId());
            if (currentPaidAmount >= businessLimit) {
                jedisClusterServer.set(stopKey, BaseConstants.MERCHANT_STOP_ORDER_SIGN);
            } else {
                jedisClusterServer.set(stopKey, "0");
            }
            // 商户限额预警消息
            merchantLimitWarnService.sendMsg(Long.valueOf(extendBean.getBusId()), currentPaidAmount, businessLimit, dateExpect, settleItem.getOrderNo(), rcsQuotaBusinessLimitResVo.getBusinessName());
        } catch (Exception e) {
            log.error("::{}::商户单日定期更新异常:{}:{}", extendBean.getBusId(), e.getMessage(), e);
        }

    }

    /**
     * 用户户单日限额 定期入库
     *
     * @param extendBean
     * @param settleItem
     */
    public void userLimitDelay(BigDecimal all, ExtendBean extendBean, SettleItem settleItem, JedisClusterPipeline jedisClusterPipeline) {
        try {
            //兼容时间取不到值
            Long opTime = settleItem.getBetTime();
            if (opTime == null) {
                opTime = settleItem.getSettleTime();
            }
            //用户赛种单日限额 和 用户单日限额 回滚
            String betDateExpect = com.panda.sport.rcs.common.DateUtils.getDateExpect(opTime);
            Long difVal = all.longValue() * (-1);
            String dayCompensationKey = LimitRedisKeys.getDayCompensationKey(betDateExpect, extendBean.getBusId(), extendBean.getUserId());
            jedisClusterPipeline.hincrByPipeline(dayCompensationKey, extendBean.getSportId(), difVal);
            jedisClusterPipeline.hincrByPipeline(dayCompensationKey, LimitRedisKeys.TOTAL_FIELD, difVal);
            jedisClusterPipeline.expirePipeline(dayCompensationKey, 26 * 60 * 60);
            log.info("::用户单日定期更新::：user ::{}::赛种:{}::difVal:{}::dayCompensationKey:{}", extendBean.getUserId(), extendBean.getSportId(), difVal,dayCompensationKey);
        } catch (Exception e) {
            log.error("::{}::用户单日定期更新异常:{}:{}", extendBean.getUserId(), e.getMessage(), e);
        }
    }

    public void initLimitDelayVo(ExtendBean extendBean, SettleItem settleItem) {
        String busId = extendBean.getBusId();
        List<LimitDelayVo> list = merchantLimitDelayMap.get(busId);
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
            merchantLimitDelayMap.put(busId, list);
        }
        LimitDelayVo limitDelayVo = new LimitDelayVo();
        limitDelayVo.setBusId(extendBean.getBusId());
        limitDelayVo.setExtendBean(extendBean);
        limitDelayVo.setSettleItem(settleItem);
        list.add(limitDelayVo);
        log.info("::{}::商户单日定期更新当前长度{}", extendBean.getItemBean().getOrderNo(), list.size());

        String userId = extendBean.getUserId();
        String userIdSportKey = userId + "-" + extendBean.getSportId();
        List<LimitDelayVo> userLimitDelayVoList = userLimitDelayMap.get(userId);
        if (CollectionUtils.isEmpty(userLimitDelayVoList)) {
            userLimitDelayVoList = new ArrayList<>();
            userLimitDelayMap.put(userIdSportKey, userLimitDelayVoList);
        }
        LimitDelayVo userLimitDelayVo = new LimitDelayVo();
        userLimitDelayVo.setUserId(userId);
        userLimitDelayVo.setSportId(extendBean.getSportId());
        userLimitDelayVo.setUserSportKey(userIdSportKey);
        userLimitDelayVo.setExtendBean(extendBean);
        userLimitDelayVo.setSettleItem(settleItem);
        userLimitDelayVoList.add(userLimitDelayVo);
        log.info("::{}::用户单日定期更新当前长度{}", extendBean.getItemBean().getOrderNo(), userLimitDelayVoList.size());
    }

    /**
     * 60秒更新一次
     */
    public void init() {
        log.info("::单日定期更新初始化完成::");
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            try {
                //商户单日定时更新
                int mapSize = merchantLimitDelayMap.size();
                log.info("::{}::商户单日定期更新当前数量", mapSize);
                //处理所有商户 本次批量处理的单日限额更新
                merchantLimitDelayMap.forEach((busId, limitDelayVoList) -> {
                    Long profit = 0L;
                    //单个商户所有订单的变动金额汇总
                    for (LimitDelayVo limitDelayVo : limitDelayVoList) {
                        profit += limitDelayVo.getExtendBean().getProfit();
                    }
                    LimitDelayVo lastVo = limitDelayVoList.get(limitDelayVoList.size() - 1);
                    ExtendBean extendBean = lastVo.getExtendBean();
                    extendBean.setProfit(profit);
                    extendBean.setBusId(busId);
                    merchantLimitDelayMap.remove(busId);
                    merchantLimitDelay(extendBean, lastVo.getSettleItem());
                });
            } catch (Exception e) {
                log.error("::商户单日定期更新异常:信息:{}:{}", e.getMessage(), e);
            }
        }, 30, 60, TimeUnit.SECONDS);

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            JedisClusterPipeline  jedisClusterPipeline = null;
            int userMapSize = userLimitDelayMap.size();
            log.info("::{}::用户单日定期更新当前数量", userMapSize);
            if(userMapSize <=0){
                return;
            }
            try {
                //用户单日定时更新
                //处理所有商户 本次批量处理的单日限额更新
                jedisClusterPipeline = new JedisClusterPipeline(jedisClusterServer.getJedisCluster());
                JedisClusterPipeline finalJedisClusterPipeline = jedisClusterPipeline;
                userLimitDelayMap.forEach((userIdSportKey, limitDelayVoList) -> {
                    BigDecimal all = new BigDecimal("0");
                    //单个商户所有订单的变动金额汇总
                    log.info("计算前参数extendBean: {}", JSONObject.toJSONString(limitDelayVoList));
                    for (LimitDelayVo limitDelayVo : limitDelayVoList) {
                        ExtendBean extendBean = limitDelayVo.getExtendBean();
                        SettleItem settleItem = limitDelayVo.getSettleItem();
                        log.info("计算前参数extendBean: {}", JSONObject.toJSONString(extendBean));
                        log.info("计算前参数settleItem: {}", JSONObject.toJSONString(settleItem));
                        if (settleItem.getSettleAmount() != null) {
                            BigDecimal dif = new BigDecimal(extendBean.getOdds()).multiply(new BigDecimal(extendBean.getOrderMoney())).subtract(new BigDecimal(settleItem.getSettleAmount()));
                            all = all.add(dif);
                        }
                    }
                    userLimitDelayMap.remove(userIdSportKey);
                    String arr[] = userIdSportKey.split("-");
                    String sportId = arr[1];
                    String userId = arr[0];
                    LimitDelayVo lastVo = limitDelayVoList.get(limitDelayVoList.size() - 1);
                    ExtendBean extendBean = lastVo.getExtendBean();
                    extendBean.setSportId(sportId);
                    extendBean.setUserId(userId);
                    log.info("计算前参数all: {}", all);
                    userLimitDelay(all, extendBean, lastVo.getSettleItem(), finalJedisClusterPipeline);
                });

            } catch (Exception e) {
                log.error("::用户单日定期更新异常:信息:{}:{}", e.getMessage(), e);
            } finally {
                if (userMapSize > 0 && jedisClusterPipeline != null) {
                    log.info("::用户单日定期更新:关闭管道连接");
                    jedisClusterPipeline.releaseConnection();
                }
            }
        }, 30, 20, TimeUnit.SECONDS);
    }
}
