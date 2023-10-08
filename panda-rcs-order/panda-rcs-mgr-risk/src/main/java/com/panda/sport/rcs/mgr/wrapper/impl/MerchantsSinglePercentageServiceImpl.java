package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.MerchantsSinglePercentageMapper;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mgr.constant.LimitRedisKeys;
import com.panda.sport.rcs.mgr.wrapper.IMerchantsSinglePercentageService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.MerchantsSinglePercentage;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.service.IRcsTournamentTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 商户单场限额监控表 服务实现类
 * </p>
 *
 * @author lithan
 * @since 2021-11-24
 */
@Service
@Slf4j
public class MerchantsSinglePercentageServiceImpl extends ServiceImpl<MerchantsSinglePercentageMapper, MerchantsSinglePercentage> implements IMerchantsSinglePercentageService {


    @Autowired
    IMerchantsSinglePercentageService merchantsSinglePercentageService;
    @Autowired
    private MerchantsSinglePercentageMapper merchantsSinglePercentageMapper;
    @Autowired
    RedisClient redisClient;
    @Autowired
    IRcsTournamentTemplateService rcsTournamentTemplateService;

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    RcsQuotaBusinessLimitMapper rcsQuotaBusinessLimitMapper;

    private static Map<String, MerchantsSinglePercentage> LIMIT_MAP = new ConcurrentHashMap<>();
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;


    private static List<String> scoreMatrixPlayIds = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
            "14", "15", "16", "27", "28", "31", "32", "33", "34", "35", "36", "68", "77", "78", "79", "80", "81", "82", "83", "84",
            "85", "86", "91", "92", "93", "94", "95", "96", "101", "102", "103", "104", "107", "108", "109", "110", "135", "136", "137",
            "141", "144", "148", "149", "150", "151", "152", "222", "223", "363", "364", "365", "366");


    @Override
    public void add(String key, MerchantsSinglePercentage value) {
        LIMIT_MAP.put(key, value);
    }

    @Override
    public void cacle(OrderBean orderBean) {
        try {
            //商户单场限额记录处理
            //Map<String, MerchantsSinglePercentage> map = redisClient.hGetAll("rcs:risk:merchants_single_percentage:all", MerchantsSinglePercentage.class);
            log.info("::{}::商户单场限额百分比更新本次处理{}:{}", orderBean.getOrderNo(), LIMIT_MAP.size(), LIMIT_MAP);
            LIMIT_MAP.forEach((k, v) -> {
                try {
                    long t1 = System.currentTimeMillis();
                    StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(v.getMatchId());
                    if(null == standardMatchInfo){
                        log.warn("::{}::商户单场限额百分比更新赛事未查到:{}", orderBean.getOrderNo(), JSONObject.toJSONString(v));
                        LIMIT_MAP.remove(k);
                        return;
                    }
                    String betDateExpect = DateUtils.getDateExpect(standardMatchInfo.getBeginTime());
                    LambdaQueryWrapper<MerchantsSinglePercentage> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(MerchantsSinglePercentage::getMatchId, v.getMatchId());
                    queryWrapper.eq(MerchantsSinglePercentage::getMerchantsId, v.getMerchantsId());
                    queryWrapper.eq(MerchantsSinglePercentage::getMatchType, v.getMatchType());
                    MerchantsSinglePercentage merchantsSinglePercentage = merchantsSinglePercentageService.getOne(queryWrapper);
                    if (merchantsSinglePercentage == null) {
                        log.warn("::{}::商户单场限额百分比更新赛事未查到:{}", orderBean.getOrderNo(), JSONObject.toJSONString(v));
                        LIMIT_MAP.remove(k);
                        return;
                    }
                    //商户单场限额 已使用多少
                    String prefix = "RCS:RISK:" + betDateExpect + ":" + v.getMerchantsId() + ":" + v.getSportId() + ":";
                    String suffix = "_{" + v.getMerchantsId() + "_" + v.getMatchId() + "}";
                    int matchType = v.getMatchType().intValue() == 2 ? 1 : 0;
                    String singleMatchInfoKey = prefix + v.getMatchId() + ":" + matchType + ":V2" + suffix;
                    String singleMatchInf = redisClient.hGet(singleMatchInfoKey, "MAX_MATCH_PAID");
                    BigDecimal use = BigDecimal.ZERO;
                    if (StringUtils.isNotBlank(singleMatchInf)) {
                        use = new BigDecimal(singleMatchInf).setScale(2, BigDecimal.ROUND_DOWN);
                        //矩阵对冲
                        use = recCalculate(use, betDateExpect, v);
                    }
                    log.info("::{}:: {}商户单场限额百分比更新已用:{}", orderBean.getOrderNo(), singleMatchInfoKey, use);
                    //读取最新的配置
                    LambdaQueryWrapper<RcsTournamentTemplate> templateWrapper = new LambdaQueryWrapper<>();
                    templateWrapper.eq(RcsTournamentTemplate::getSportId, merchantsSinglePercentage.getSportId());
                    templateWrapper.eq(RcsTournamentTemplate::getType, 3);
                    templateWrapper.eq(RcsTournamentTemplate::getTypeVal, merchantsSinglePercentage.getMatchId());
                    templateWrapper.eq(RcsTournamentTemplate::getMatchType, merchantsSinglePercentage.getMatchType() == 2 ? 0 : 1);
                    RcsTournamentTemplate rcsTournamentTemplate = rcsTournamentTemplateService.getOne(templateWrapper);
                    merchantsSinglePercentage.setMatchLimit(rcsTournamentTemplate.getBusinesMatchPayVal());
                    log.info("::{}:: {}商户单场限额百分比更新模板:{}", orderBean.getOrderNo(), singleMatchInfoKey, rcsTournamentTemplate.getBusinesMatchPayVal());
                    //乘以比例
                    LambdaQueryWrapper<RcsQuotaBusinessLimit> businessLimitLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    businessLimitLambdaQueryWrapper.eq(RcsQuotaBusinessLimit::getBusinessId, String.valueOf(v.getMerchantsId()));
                    List<RcsQuotaBusinessLimit> quotaBusinessLimitList = rcsQuotaBusinessLimitMapper.selectList(businessLimitLambdaQueryWrapper);
                    //商户单场限额比例
                    BigDecimal businessSingleDayGameProportion = new BigDecimal("1");
                    if (ObjectUtils.isNotEmpty(quotaBusinessLimitList)) {
                        businessSingleDayGameProportion = quotaBusinessLimitList.get(0).getBusinessSingleDayGameProportion();
                    }
                    Long limit = businessSingleDayGameProportion.multiply(new BigDecimal(merchantsSinglePercentage.getMatchLimit())).longValue();
                    merchantsSinglePercentage.setMatchLimit(limit);
                    log.info("::{}:: {}商户单场限额百分比更新最新配置:{}:比例:{}", orderBean.getOrderNo(), singleMatchInfoKey, limit, businessSingleDayGameProportion);
                    //计算比例 并更新库
                    BigDecimal rate = use.divide(new BigDecimal(merchantsSinglePercentage.getMatchLimit()), 2, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN);
                    merchantsSinglePercentage.setPercentage(rate);
                    merchantsSinglePercentageService.updateById(merchantsSinglePercentage);
                    LIMIT_MAP.remove(k);
                    long t2 = System.currentTimeMillis();
                    log.info("::{}:: 商户单场限额百分比更新完成:{}:{}:耗时:{}", orderBean.getOrderNo(), JSONObject.toJSONString(merchantsSinglePercentage), singleMatchInfoKey, (t2 - t1));

                    //推送mango/telegram 商户预警  需求-2088
                    String key = null;
                    if (rate.compareTo(new BigDecimal("100")) >= 0) {
                        key = "100";
                    }
                    if (rate.compareTo(new BigDecimal("80")) >= 0 && rate.compareTo(new BigDecimal("100")) < 0) {
                        key = "80";
                    }
                    if (key != null) {
                        sendMerchantOverSingleMatchLimit(key, String.valueOf(v.getMatchId()), String.valueOf(v.getMerchantsId()),
                                v.getMerchantsName(), limit, use, rate, standardMatchInfo.getMatchManageId());
                    }
                } catch (Exception e) {
                    log.error("::{}:: 商户单场限额百分比更新异常:{}", orderBean.getOrderNo(), JSONObject.toJSONString(v),e);
                }
            });

        } catch (Exception e) {
            log.error("::{}::商户单场限额百分比更新异常:{}:{}", orderBean.getOrderNo(), e.getMessage(), e);
        } finally {
        }

    }


    private BigDecimal recCalculate(BigDecimal used, String dateExpect, MerchantsSinglePercentage v) {
        log.info("::{}::{}商户单场限额百分比更新已用进行对冲计算", v.getOrderNo(), v.getMerchantsId());
        try {
            Map<String, String> map;
            String suffix = "_{" + v.getMerchantsId() + "_" + v.getMatchId() + "}";
            String prefix = "RCS:RISK:" + dateExpect + ":" + v.getMerchantsId() + ":" + v.getSportId() + ":";
            if (scoreMatrixPlayIds.contains(v.getPlayId())) {
                String singleMatchInfoKey = prefix + v.getMatchId() + ":V2" + suffix;
                map = redisClient.hGetAll(singleMatchInfoKey, Map.class);
                String all_keys = map.get("ALL_KEYS");
                //矩阵最大赔付
                BigDecimal recMaxPaidMoney = new BigDecimal(Optional.ofNullable(map.get("REC_MAX_PAID")).orElse("0"));
                BigDecimal lastUsed = used.multiply(recMaxPaidMoney);
                if (StringUtils.isNotBlank(all_keys)) {
                    //赔付矩阵数据
                    String[] allRecArray = all_keys.split(",");
                    //订单矩阵数据
                    Long[][] recVal = JSONObject.parseObject(v.getRecVal(), new TypeReference<Long[][]>() {
                    });
                    int j = 0;
                    BigDecimal recMinMoney = new BigDecimal(Long.MAX_VALUE);
                    BigDecimal recAllMinMoney = new BigDecimal(Long.MAX_VALUE);
                    for (int i = 0; i < allRecArray.length; i++) {
                        int index = i % 13;
                        BigDecimal recMinMoneyTemp = new BigDecimal(allRecArray[i]);
                        if (recVal[j][index] < 0) {//亏损/庄家赔
                            if (recMinMoneyTemp.compareTo(recMinMoney) < 0) {
                                recMinMoney = recMinMoneyTemp;
                            }
                        }
                        if (recMinMoneyTemp.compareTo(recAllMinMoney) < 0) {
                            recAllMinMoney = recMinMoneyTemp;
                        }
                        if ((i + 1) % 13 == 0) j++;
                    }
                    //矩阵对冲
                    BigDecimal recPay = recAllMinMoney.subtract(recMinMoney);
                    lastUsed = lastUsed.subtract(recPay);
                }
                log.info("::{}::{}商户单场限额百分比更新已用-使用矩阵进行对冲计算返回:{},玩法:{}", v.getOrderNo(), v.getMerchantsId(), lastUsed, v.getPlayId());
                return lastUsed;
            } else {
                String singleMatchMarketKey = prefix + v.getMatchId() + ":" + v.getMatchId() + ":" + v.getMarketId() + suffix;
                BigDecimal maxMatchPlayPaidMoney = new BigDecimal(Optional.ofNullable(redisClient.hGet(singleMatchMarketKey, "MAX_PLAY_PAID")).orElse("0"));
                BigDecimal maxMatchPlayBetAmountMoney = new BigDecimal(Optional.ofNullable(redisClient.hGet(singleMatchMarketKey, "allOrderMoney")).orElse("0"));
                BigDecimal maxMatchPlayOptionPaidMoney = new BigDecimal(Optional.ofNullable(redisClient.hGet(singleMatchMarketKey, v.getOptionId())).orElse("0"));
                //投注项对冲
                BigDecimal optionMoney = maxMatchPlayOptionPaidMoney.subtract(maxMatchPlayBetAmountMoney);
                BigDecimal lastUsed = used.subtract(maxMatchPlayPaidMoney).subtract(optionMoney);
                log.info("::{}::{}商户单场限额百分比更新已用-使用玩法进行对冲计算返回:{},玩法:{}", v.getOrderNo(), v.getMerchantsId(), lastUsed, v.getPlayId());
                return lastUsed;
            }
        } catch (Exception e) {
            log.info("::{}::{}商户单场限额百分比更新已用进行对冲计算异常:", v.getOrderNo(), v.getMerchantsId(), e);
            return used;
        }
    }

    @Value("${spring.profiles.active:pro}")
    private String env;

    private void sendMerchantOverSingleMatchLimit(String rateKey, String matchId, String merchantsId, String merchantsName, Long limit, BigDecimal used,
                                                  BigDecimal rate, String matchManageId) {
        String alertKey = LimitRedisKeys.getMerchantAlertKey(matchId) + ":" + rateKey;
        Long num = redisClient.hincrBy(alertKey, merchantsId, 1L);
        redisClient.expireKey(alertKey, 8 * 24 * 60 * 60);
        log.info("::{}::商户单场限额超出发送预警信息,key:{},num:{},rateKey:{}", merchantsId, alertKey, num, rateKey);
        if (("100".equals(rateKey) && num <= 2) || ("80".equals(rateKey) && num <= 1)) {
            List<String> list = new ArrayList<>();
            DecimalFormat format = new DecimalFormat("#,###");
            String str = "[AlertType]：MerchantOverSingleMatchLimit商户单场限额告警\n" +
                    "[Env]：" + env + "\n" +
                    "[Alert Time]：" + DateUtils.transferLongToDateStrings(System.currentTimeMillis()) + "\n" +
                    "[Merchant ID]：" + merchantsId + "\n" +
                    "[Merchant Name]：" + merchantsName + "\n" +
                    "[Match ID]：" + matchManageId + "\n" +
                    "[MerchantSingleMatchLimit]：" + format.format(limit) + "\n" +
                    "[MerchantSingleMatchLimitUsed]：" + format.format(used.divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_DOWN)) + "\n" +
                    "[MerchantSingleMatchLimitUsed%]：" + rate + "%";
            list.add(str);
            JSONObject param = new JSONObject(new LinkedHashMap<>());
            param.put("data", list);
            //需求编号
            param.put("dataSourceCode", "2088");
            //linkId
            String job = "ScoreAlertJob:" + merchantsId;
            param.put("linkId", job);
            //推送到数据支撑，进行mango/telegram预警
            producerSendMessageUtils.sendMessage("PA_COMMON_WARN_INFO", "MerchantOverSingleMatchLimit", job, param);
        }
    }

    @PostConstruct
    public void init() {
        log.info("::商户单场限额百分比更新初始化完成::");
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            try {
                OrderBean orderBean = new OrderBean();
                orderBean.setOrderNo(System.currentTimeMillis() + "");
                cacle(orderBean);
            } catch (Exception e) {
                log.error("::商户单场限额百分比更新异常:信息:{}", e.getMessage(), e);
            }
        }, 30, 60, TimeUnit.SECONDS);
    }
}
