package com.panda.rcs.stray.limit.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.rcs.stray.limit.entity.constant.MqConstant;
import com.panda.rcs.stray.limit.entity.constant.RedisKeyConstant;
import com.panda.rcs.stray.limit.mq.RcsConsumer;
import com.panda.rcs.stray.limit.utils.RealTimeControlUtils;
import com.panda.rcs.stray.limit.wrapper.CommonService;
import com.panda.rcs.stray.limit.wrapper.MerchantLimitWarnService;
import com.panda.sport.data.rcs.dto.OrderDetailPO;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.RedisCmdEnum;
import com.panda.sport.rcs.pojo.limit.RedisUpdateVo;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.utils.SeriesTypeUtils;
import com.panda.sport.rcs.vo.SettleItemPO;
import com.panda.sport.rcs.wrapper.limit.RcsQuotaBusinessLimitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 结算派彩
 * @Author : Paca
 * @Date : 2022-03-29 11:08
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = RcsConstant.OSMC_SETTLE_RESULT_SDK,
        consumerGroup = MqConstant.PREFIX + RcsConstant.OSMC_SETTLE_RESULT_SDK + MqConstant.SUFFIX,
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class SdkSettleConsumer extends RcsConsumer<SettleItemPO> {

    @Autowired
    private RcsQuotaBusinessLimitService rcsQuotaBusinessLimitService;

    @Autowired
    private CommonService commonService;
    @Autowired
    private MerchantLimitWarnService merchantLimitWarnService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RealTimeControlUtils realTimeControlUtils;

    @Override
    protected String getTopic() {
        return RcsConstant.OSMC_SETTLE_RESULT_SDK;
    }

    private boolean checkSettleMsg(SettleItemPO settleItem) {

        String orderNo = settleItem.getOrderNo();
        Integer seriesType = settleItem.getSeriesType();
        List<OrderDetailPO> orderDetailList = settleItem.getOrderDetailRisk();
        if (seriesType == null || seriesType <= 1) {
            log.info("::{}::单关不处理,seriesType={}", orderNo, seriesType);
            return false;
        }
        if (CollectionUtils.isEmpty(orderDetailList)) {
            log.warn("::{}:: 注单投注项详情为空!", orderNo);
            return false;
        }
        Integer matchType = orderDetailList.get(0).getMatchType();
        if (matchType != null && matchType == 3) {
            log.info("::{}::冠军玩法不处理matchType={}", orderNo, matchType);
            return false;
        }
//        TOrder order = orderService.getByOrderNo(orderNo);
//        if (order == null) {
//            log.warn("订单不存在：orderNo = ::{}::", orderNo);
//            return false;
//        }
        if (NumberUtils.INTEGER_TWO.equals(settleItem.getLimitType())) {
            log.info(" ::{}::信用模式不处理!", orderNo);
            return false;
        }
        if (settleItem.getSettleTime() == null) {
            settleItem.setSettleTime(System.currentTimeMillis());
            log.warn("::{}::结算时间为空!", orderNo);
        }
        if (settleItem.getOutCome() == null) {
            settleItem.setOutCome(0);
            log.info("::{}::结算结果为空!", orderNo);
        }
        if (settleItem.getSettleAmount() == null) {
            settleItem.setSettleAmount(0L);
            log.info("::{}::结算金额为空", orderNo);
        }
        if (settleItem.getBetAmount() == null) {
            settleItem.setBetAmount(0L);
            log.info("::{}::投注金额为空!", orderNo);
        }
        return true;
    }

    @Override
    protected Boolean handleMs(SettleItemPO settleItemPO) {

        if (!realTimeControlUtils.isSettleHandleStatus()) {
            log.info("::{}::串关结算回滚开关关闭，不予处理", settleItemPO.getOrderNo());
            return true;
        }

        if (!checkSettleMsg(settleItemPO)) {
            return true;
        }
        Long merchantId = settleItemPO.getMerchantId();
        Long userId = settleItemPO.getUid();
        String orderNo = settleItemPO.getOrderNo();
        String dateExpect = DateUtils.getDateExpect(settleItemPO.getSettleTime());
        long profitAmount = getProfitAmount(settleItemPO);
        RcsQuotaBusinessLimitResVo businessLimit = rcsQuotaBusinessLimitService.getByMerchantIdFromRedis(merchantId);
        // 特殊VIP不计入商户单日
        if (!commonService.isSpecialVipLimit(String.valueOf(userId))) {
            handleMerchantLimit(merchantId, dateExpect, orderNo, profitAmount, businessLimit.getBusinessSingleDayLimit(), businessLimit.getBusinessName());
            handleMerchantSeriesLimit(merchantId, dateExpect, orderNo, profitAmount, businessLimit.getBusinessSingleDaySeriesLimit(), businessLimit.getBusinessName());
        }

        // 串关回滚
        String recordKey = String.format(RedisKeyConstant.SERIES_REDIS_UPDATE_RECORD_KEY, orderNo);
        String record = redisClient.get(recordKey);
        log.info("::{}::串关Redis更新记录：key={},value={}", orderNo, recordKey, record);
        if (StringUtils.isBlank(record)) {
            return true;
        }
        List<RedisUpdateVo> redisUpdateList = JSON.parseArray(record, RedisUpdateVo.class);
        RedisUpdateVo newModeInfo = getNewModeInfo(redisUpdateList);
        if (newModeInfo != null) {
            boolean isHighRisk = "1".equals(newModeInfo.getField());
            newModeCalLimit(settleItemPO, redisUpdateList, profitAmount, isHighRisk);
        } else {
            oldModeCalLimit(redisUpdateList, profitAmount, orderNo);
        }
        redisClient.delete(String.format(RedisKeyConstant.SERIES_REDIS_UPDATE_RECORD_KEY, orderNo));
        return true;
    }

    /**
     * 当用户结算结果为赢，那么用户单日串关总可用额度不做处理
     */
    public List<RedisUpdateVo> handlerSpecialUserTotalPaid(SettleItemPO settleItemPO, List<RedisUpdateVo> redisUpdateList, long profitAmount) {
        if (CollectionUtils.isEmpty(redisUpdateList)) {
            return redisUpdateList;
        }
        String key = "risk:trade:rcs_user_special_bet_limit_config:" + settleItemPO.getUid();
        String type = redisClient.hGet(key, "type");
        if (StringUtils.isBlank(type) || (!"2".equals(type) && !"3".equals(type))) {//不是特殊限额用户
            return redisUpdateList;
        }
        String percentage = redisClient.hGet(key, "percentage");
        String specialUserAmountStr = redisClient.hGet(key, "2_-1_single_game_claim_limit");
        if (StringUtils.isBlank(percentage) && StringUtils.isBlank(specialUserAmountStr)) {//特殊限额数据为空
            return redisUpdateList;
        }
        //过滤T1 用户单日串关总可用额度
        List<RedisUpdateVo> redisUpdateVos = redisUpdateList.stream().filter(e -> !(e.getKey().contains("single_day_stray_total_merchant") &&
                !e.getKey().contains("race") && !e.getKey().contains("seriesType"))).collect(Collectors.toList());
        log.info("::{}::低风险串关-用户结算结果为赢：过滤前: {},过滤T1 用户单日串关总可用额度={}", settleItemPO.getOrderNo(), JSON.toJSONString(redisUpdateList), JSON.toJSONString(redisUpdateVos));
        //扣减用户单日串关类型可赔付额度 T3减去B
        Long merchantId = settleItemPO.getMerchantId();
        Long userId = settleItemPO.getUid();
        String dateExpect = DateUtils.getDateExpect(settleItemPO.getSettleTime());
        // 获取M串中的M
        Integer seriesNum = SeriesTypeUtils.getSeriesType(settleItemPO.getSeriesType());
        // 单日串关类型赔付限额
        String dailySeriesTypeLimitKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_SERIES_KEY, merchantId, userId, seriesNum, dateExpect);
        redisClient.incrBy(dailySeriesTypeLimitKey, profitAmount);
        long profit = profitAmount / settleItemPO.getOrderDetailRisk().size();
        settleItemPO.getOrderDetailRisk().forEach(orderDetail -> {
            // 计入串关每个赛种已用额度
            String sportTypeKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_RACE_KEY, merchantId, userId, orderDetail.getSportId(), dateExpect);
            redisClient.incrBy(sportTypeKey, profit);
            redisClient.expireKey(sportTypeKey, RedisKeyConstant.COMPETITION_EXPIRY_KEY);
        });
        return redisUpdateVos;
    }


    /**
     * 当用户结算结果为输，那么用户单日串关总可用额度新增额度
     */
    public List<RedisUpdateVo> handlerSpecialUserTotalPaidAdd(SettleItemPO settleItemPO, List<RedisUpdateVo> redisUpdateList) {
        if (CollectionUtils.isEmpty(redisUpdateList)) {
            return null;
        }
        String key = "risk:trade:rcs_user_special_bet_limit_config:" + settleItemPO.getUid();
        String type = redisClient.hGet(key, "type");
        if (StringUtils.isBlank(type) || (!"2".equals(type) && !"3".equals(type))) {//不是特殊限额用户
            return null;
        }
        String percentage = redisClient.hGet(key, "percentage");
        String specialUserAmountStr = redisClient.hGet(key, "2_-1_single_game_claim_limit");
        if (StringUtils.isBlank(percentage) && StringUtils.isBlank(specialUserAmountStr)) {//特殊限额数据为空
            return null;
        }
        //过滤T1 用户单日串关总可用额度
        List<RedisUpdateVo> redisUpdateVos = redisUpdateList.stream().filter(e -> e.getKey().contains("single_day_stray_total_merchant") &&
                !e.getKey().contains("race") && !e.getKey().contains("seriesType")).collect(Collectors.toList());
        log.info("::{}::低风险串关-用户结算结果为输：T1增加注单金额={}", settleItemPO.getOrderNo(), JSON.toJSONString(redisUpdateVos));
        return redisUpdateVos;
    }


    private void newModeCalLimit(SettleItemPO settleItemPO, List<RedisUpdateVo> redisUpdateList, long profitAmount, boolean isHighRisk) {
        log.info("::{}::新模式：profitAmount={},isHighRisk={}", settleItemPO.getOrderNo(), profitAmount, isHighRisk);
        // 串关单日限额 和 单关限额 分开处理
        if (settleItemPO.getOutCome() == 3 || settleItemPO.getOutCome() == 6 || settleItemPO.getOutCome() == 2) {
            //List<RedisUpdateVo> list = handlerSpecialUserTotalPaid(settleItemPO, redisUpdateList);
            // 输 | 输一半 | 走水
            rollbackDaily(redisUpdateList);
        }
        //赢
        if (settleItemPO.getOutCome() == 4) {
            if (!isHighRisk) {//低风险注单
                List<RedisUpdateVo> list = handlerSpecialUserTotalPaid(settleItemPO, redisUpdateList, profitAmount);
                if (!CollectionUtils.isEmpty(list)) {
                    rollbackDaily(list);
                }
            }
        }
        if (isHighRisk) {
            // 高赔才会累计单日串关限额，实际盈利计算
            //rollbackSingleMatch(redisUpdateList);
            rollbackSingleMatchV2(redisUpdateList,settleItemPO);
            if (profitAmount == 0L) {
                return;
            }
            if (settleItemPO.getOutCome() == 6 || settleItemPO.getOutCome() == 2) {
                //  输一半 | 走水
                Long merchantId = settleItemPO.getMerchantId();
                Long userId = settleItemPO.getUid();
                String dateExpect = DateUtils.getDateExpect(settleItemPO.getSettleTime());
                // 获取M串中的M
                Integer seriesNum = SeriesTypeUtils.getSeriesType(settleItemPO.getSeriesType());
                // 单日串关赔付总限额
                String dailySeriesTotalLimitKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_KEY, merchantId, userId, dateExpect);
                redisClient.incrBy(dailySeriesTotalLimitKey, profitAmount);
                redisClient.expireKey(dailySeriesTotalLimitKey, RedisKeyConstant.COMPETITION_EXPIRY_KEY);
                // 单日串关类型赔付限额
                String dailySeriesTypeLimitKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_SERIES_KEY, merchantId, userId, seriesNum, dateExpect);
                redisClient.incrBy(dailySeriesTypeLimitKey, profitAmount);
                redisClient.expireKey(dailySeriesTypeLimitKey, RedisKeyConstant.COMPETITION_EXPIRY_KEY);
                long profit = profitAmount / settleItemPO.getOrderDetailRisk().size();
                settleItemPO.getOrderDetailRisk().forEach(orderDetail -> {
                    // 计入串关每个赛种已用额度
                    String sportTypeKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_RACE_KEY, merchantId, userId, orderDetail.getSportId(), dateExpect);
                    redisClient.incrBy(sportTypeKey, profit);
                    redisClient.expireKey(sportTypeKey, RedisKeyConstant.COMPETITION_EXPIRY_KEY);
                });
            }
            // 赢 / 赢半
            if (settleItemPO.getOutCome() == 5 || settleItemPO.getOutCome() == 4) {
                profitAmount = new BigDecimal(profitAmount).subtract(new BigDecimal(settleItemPO.getOrderDetailRisk().get(0).getMaxWinAmount())).longValue();
                //赢了全部 无需回滚额度
                boolean isWinHalf = true;
                for (OrderDetailPO e : settleItemPO.getOrderDetailRisk()) {
                    if (e.getBetResult() != 4) {
                        isWinHalf = false;
                    }
                }
                //if (new BigDecimal(profitAmount).compareTo(new BigDecimal(0)) > -1) {
                if (isWinHalf) {
                    return;
                }
//                BigDecimal value = CommonUtils.toBigDecimal(String.valueOf(profitAmount), BigDecimal.ZERO).negate();
                // 串关 赢半 处理回滚
                Long merchantId = settleItemPO.getMerchantId();
                Long userId = settleItemPO.getUid();
                String dateExpect = DateUtils.getDateExpect(settleItemPO.getSettleTime());
                // 获取M串中的M？
                Integer seriesNum = SeriesTypeUtils.getSeriesType(settleItemPO.getSeriesType());
                // 单日串关赔付总限额
                String dailySeriesTotalLimitKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_KEY, merchantId, userId, dateExpect);
                Double field = redisClient.incrByFloat(dailySeriesTotalLimitKey, Double.parseDouble(String.valueOf(profitAmount)));
                redisClient.expireKey(dailySeriesTotalLimitKey, RedisKeyConstant.COMPETITION_EXPIRY_KEY);
                log.info("::{}::新模式回滚单日串关赔付总限额key：{},profitAmount:{},回滚后field:{}", settleItemPO.getOrderNo(), dailySeriesTotalLimitKey, profitAmount, field);
                // 单日串关类型赔付限额
                String dailySeriesTypeLimitKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_SERIES_KEY, merchantId, userId, seriesNum, dateExpect);
                field = redisClient.incrByFloat(dailySeriesTypeLimitKey, Double.parseDouble(String.valueOf(profitAmount)));
                redisClient.expireKey(dailySeriesTypeLimitKey, RedisKeyConstant.COMPETITION_EXPIRY_KEY);
                log.info("::{}::新模式单日串关类型赔付限额key：{},profitAmount:{},回滚后field:{}", settleItemPO.getOrderNo(), dailySeriesTypeLimitKey, profitAmount, field);
                long profit = profitAmount / settleItemPO.getOrderDetailRisk().size();

                settleItemPO.getOrderDetailRisk().forEach(orderDetail -> {
                    // 计入串关每个赛种已用额度
                    Double finalField;
                    String sportTypeKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_RACE_KEY, merchantId, userId, orderDetail.getSportId(), dateExpect);
                    finalField = redisClient.incrByFloat(sportTypeKey, Double.parseDouble(String.valueOf(profit)));
                    redisClient.expireKey(sportTypeKey, RedisKeyConstant.COMPETITION_EXPIRY_KEY);
                    log.info("::{}::新模式串关每个赛种已用额度key：{},profitAmount:{},回滚后field:{}", settleItemPO.getOrderNo(), sportTypeKey, profit, finalField);
                });
            }
        }
    }

    /**
     * 回滚单关限额，W1、W2
     *
     * @param redisUpdateList
     */
    private void rollbackSingleMatch(List<RedisUpdateVo> redisUpdateList) {
        if (CollectionUtils.isNotEmpty(redisUpdateList)) {
            redisUpdateList.forEach(vo -> {
                String key = vo.getKey();
                if (!key.contains("single_day_stray_total_merchant")) {
                    BigDecimal value = CommonUtils.toBigDecimal(vo.getValue(), BigDecimal.ZERO).negate();
                    commonService.exeIncrByCmd(vo.getCmd(), key, vo.getField(), value);
                }
            });
        }
    }

    /**
     * 赛事单场特殊处理
     * @param redisUpdateList
     * @param settleItemPO
     */
    private void rollbackSingleMatchV2(List<RedisUpdateVo> redisUpdateList,SettleItemPO settleItemPO) {
        if (CollectionUtils.isNotEmpty(redisUpdateList)) {
            Long merchantId = settleItemPO.getMerchantId();
            redisUpdateList.forEach(vo -> {
                String key = vo.getKey();
                if (!key.contains("single_day_stray_total_merchant")) {
                    boolean isWinAll =false;
                    for(OrderDetailPO po:settleItemPO.getOrderDetailRisk()){
                        //"rcs:user:507994179846300022:bus:2:match:3531042"
                        String singleMatchKey = String.format("rcs:user:%s:bus:%s:match:%s",po.getUid(),merchantId,po.getMatchId());
                        if(key.equals(singleMatchKey) && po.getBetResult()==4){
                            isWinAll = true;
                            log.info("::{}::新模式：串关回滚,赛事：{}为赢，不回滚", settleItemPO.getOrderNo(), po.getMatchId());
                        }
                    }
                    //全赢不回滚赛事单场
                    if(!isWinAll){
                        BigDecimal value = CommonUtils.toBigDecimal(vo.getValue(), BigDecimal.ZERO).negate();
                        commonService.exeIncrByCmd(vo.getCmd(), key, vo.getField(), value);
                    }
                }
            });
        }
    }


    /**
     * 回滚单日串关限额，T1、T2、T3
     *
     * @param redisUpdateList
     */
    private void rollbackDaily(List<RedisUpdateVo> redisUpdateList) {
        if (CollectionUtils.isNotEmpty(redisUpdateList)) {
            redisUpdateList.forEach(vo -> {
                String key = vo.getKey();
                if (key.contains("single_day_stray_total_merchant")) {
                    BigDecimal value = CommonUtils.toBigDecimal(vo.getValue(), BigDecimal.ZERO).negate();
                    commonService.exeIncrByCmd(vo.getCmd(), key, vo.getField(), value);
                }
            });
        }
    }

    private void oldModeCalLimit(List<RedisUpdateVo> redisUpdateList, long profitAmount, String oderNO) {
        log.info("::{}::旧模式：profitAmount={}", oderNO, profitAmount);
        if (CollectionUtils.isEmpty(redisUpdateList)) {
            return;
        }
        redisUpdateList.forEach(vo -> {
            String key = vo.getKey();
            String field = vo.getField();
            BigDecimal value = CommonUtils.toBigDecimal(vo.getValue(), BigDecimal.ZERO).negate();
            String cmd = vo.getCmd();
            if (key.contains("rcs:limit:crossDayCompensation") || key.contains("rcs:limit:dayCompensation")) {
                // 单日已用额度按照实际赔付来
                commonService.exeIncrByCmd(cmd, key, field, value.add(new BigDecimal(profitAmount)));
            } else {
                if (profitAmount <= 0) {
                    // 输、走水，已用额度回滚
                    commonService.exeIncrByCmd(cmd, key, field, value);
                }
            }
        });
    }

    private RedisUpdateVo getNewModeInfo(List<RedisUpdateVo> redisUpdateList) {
        for (RedisUpdateVo vo : redisUpdateList) {
            if (RedisCmdEnum.LIMIT_MODE.isYes(vo.getCmd())) {
                return vo;
            }
        }
        return null;
    }

    /**
     * 获取注单盈利金额
     *
     * @param settleItem
     * @return
     */
    private long getProfitAmount(SettleItem settleItem) {
        // 2-走水 或 9-拒单
        if (settleItem.getOutCome() == 2 || settleItem.getOutCome() == 9) {
            return 0L;
        }
        // 3-输（平台赢本金）
        if (settleItem.getOutCome() == 3) {
            return settleItem.getBetAmount() * -1;
        }
        // 4-赢 或 5-赢一半
        if (settleItem.getOutCome() == 4 || settleItem.getOutCome() == 5) {
            return settleItem.getSettleAmount() - settleItem.getBetAmount();
        }
        // 6-输一半
        if (settleItem.getOutCome() == 6) {
            return settleItem.getSettleAmount() * -1;
        }
        return settleItem.getSettleAmount() - settleItem.getBetAmount();
    }

    /**
     * 累加 商户单日已用限额
     *
     * @param dateExpect
     * @param merchantId
     * @param profitAmount
     * @return
     */
    private Long merchantDailyUsedLimitIncrBy(String dateExpect, Long merchantId, long profitAmount) {
        String key = String.format(RedisKeyConstant.PAID_DATE_BUS_REDIS_CACHE, dateExpect, merchantId);
        Long value = redisClient.incrBy(key, profitAmount);
        log.info("累加商户单日已用限额：key={},value={},result={}", key, profitAmount, value);
        redisClient.expireKey(key, 30 * 24 * 60 * 60);
        if (value == null) {
            value = 0L;
        }
        return value;
    }

    /**
     * 累加 商户单日串关已用限额
     *
     * @param dateExpect
     * @param merchantId
     * @param profitAmount
     * @return
     */
    private Long merchantDailySeriesUsedLimitIncrBy(String dateExpect, Long merchantId, long profitAmount) {
        String key = String.format(RedisKeyConstant.PAID_DATE_BUS_SERIES_REDIS_CACHE, dateExpect, merchantId);
        Long value = redisClient.incrBy(key, profitAmount);
        log.info("累加商户单日串关已用限额：key={},value={},result={}", key, profitAmount, value);
        redisClient.expireKey(key, 30 * 24 * 60 * 60);
        if (value == null) {
            value = 0L;
        }
        return value;
    }

    /**
     * 处理 商户单日限额
     *
     * @param merchantId   商户ID
     * @param dateExpect   账务日
     * @param orderNo      订单号
     * @param profitAmount 盈利金额
     * @param configLimit  商户单日限额配置
     */
    private void handleMerchantLimit(Long merchantId, String dateExpect, String orderNo, long profitAmount, long configLimit, String busName) {
        // 累加 商户单日已用限额
        Long usedLimit = merchantDailyUsedLimitIncrBy(dateExpect, merchantId, profitAmount);
        // 商户单日限额预警消息
        merchantLimitWarnService.sendMsg(merchantId, usedLimit, configLimit, dateExpect, orderNo, 0, busName);
        String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, dateExpect, merchantId);
        if (usedLimit >= configLimit) {
            redisClient.set(stopKey, "1");
        } else {
            redisClient.set(stopKey, "0");
        }
        redisClient.expireKey(stopKey, 30 * 24 * 60 * 60);
    }

    /**
     * 处理 商户单日串关限额
     *
     * @param merchantId   商户ID
     * @param dateExpect   账务日
     * @param orderNo      订单号
     * @param profitAmount 盈利金额
     * @param configLimit  商户单日串关限额配置
     */
    private void handleMerchantSeriesLimit(Long merchantId, String dateExpect, String orderNo, long profitAmount, long configLimit, String busName) {
        // 累加商户单日 串关 已用限额
        Long usedLimit = merchantDailySeriesUsedLimitIncrBy(dateExpect, merchantId, profitAmount);
        // 商户单日串关限额预警消息
        merchantLimitWarnService.sendMsg(merchantId, usedLimit, configLimit, dateExpect, orderNo, 1, busName);
    }
}
