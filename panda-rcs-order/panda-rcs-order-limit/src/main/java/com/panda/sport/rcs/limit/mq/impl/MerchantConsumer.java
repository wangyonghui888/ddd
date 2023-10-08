package com.panda.sport.rcs.limit.mq.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.limit.constants.Constants;
import com.panda.sport.rcs.limit.mq.bean.Merchant;
import com.panda.sport.rcs.mapper.RcsCodeMapper;
import com.panda.sport.rcs.mapper.RcsOmitConfigMapper;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitMapper;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessRateMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsCode;
import com.panda.sport.rcs.pojo.RcsMissedOrderConfigStatus;
import com.panda.sport.rcs.pojo.RcsOmitConfig;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.panda.sport.rcs.constants.RedisKey.REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY;

/**
 * 同步商户信息公共类
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MERCHANT_INFO_RESULT",
        consumerGroup = "RCS_DATA_MERCHANT_INFO_RESULT_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class MerchantConsumer implements RocketMQListener<Merchant>, RocketMQPushConsumerLifecycleListener {


    @Autowired
    private RcsCodeMapper rcsCodeMapper;
    @Autowired
    private RcsQuotaBusinessLimitMapper rcsQuotaBusinessLimitMapper;

    @Autowired
    private RcsOmitConfigMapper rcsOmitConfigMapper;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    RcsQuotaBusinessRateMapper rcsQuotaBusinessRateMapper;

    @Autowired
    private RedisClient redisClient;

    @Override
    public void onMessage(Merchant data) {
        try {
            log.info("收到商户同步数据,id::{}::,data:{}", "RDMIRG_" + data.getId(), JSON.toJSONString(data));
            if (data.getAgentLevel() == 0 || data.getAgentLevel() == 2) {
                Map<String, Object> columnMap = new HashMap<>();
                columnMap.put("business_id", data.getId());
                //商户限额模式 传参 0 普通 1 信用限额 ， 对应db limitType字段 1 普通  2信用限额
                Integer limitFlag = data.getMerchantTag();
                if (limitFlag == null) {
                    limitFlag = 1;
                } else {
                    limitFlag += 1;
                }
                RcsQuotaBusinessLimit rcsQuotaBusinessLimitData = rcsQuotaBusinessLimitMapper.selectOne(new LambdaQueryWrapper<RcsQuotaBusinessLimit>().eq(RcsQuotaBusinessLimit::getBusinessId, data.getId()));
                BigDecimal userSingleStrayLimit = Constants.USER_SINGLE_STRAY_LIMIT.multiply(Constants.DAY_COMPENSATION_PROPORTION);
                if (Objects.isNull(rcsQuotaBusinessLimitData)) {
                    RcsQuotaBusinessLimit rcsQuotaBusinessLimit = new RcsQuotaBusinessLimit();
                    rcsQuotaBusinessLimit.setBusinessId(data.getId());
                    rcsQuotaBusinessLimit.setStatus(data.getStatus());
                    rcsQuotaBusinessLimit.setBusinessName(data.getMerchantCode());
                    //商户单日限额比例
                    rcsQuotaBusinessLimit.setBusinessSingleDayLimitProportion(Constants.DAY_COMPENSATION_PROPORTION);
                    //商户单日限额
                    long businessSingleDayLimit = Constants.BUSINESS_SINGLE_DAY_LIMIT.multiply(
                            rcsQuotaBusinessLimit.getBusinessSingleDayLimitProportion()).longValue();
                    rcsQuotaBusinessLimit.setBusinessSingleDayLimit(businessSingleDayLimit);

                    //商户单日串关限额比例
                    rcsQuotaBusinessLimit.setBusinessSingleDaySeriesLimitProportion(Constants.DAY_SERIES_COMPENSATION_PROPORTION);
                    //商户单日串关限额
                    long businessSingleDaySeriesLimit = Constants.BUSINESS_SINGLE_DAY_SERIES_LIMIT.multiply(
                            rcsQuotaBusinessLimit.getBusinessSingleDaySeriesLimitProportion()).longValue();
                    rcsQuotaBusinessLimit.setBusinessSingleDaySeriesLimit(businessSingleDaySeriesLimit);

                    //商户单场限额比例
                    rcsQuotaBusinessLimit.setBusinessSingleDayGameProportion(Constants.DAY_COMPENSATION_PROPORTION);
                    //用户单关累计限额比例
                    rcsQuotaBusinessLimit.setUserQuotaRatio(Constants.DAY_COMPENSATION_PROPORTION);
                    //用户单关单注限额比例
                    rcsQuotaBusinessLimit.setUserQuotaBetRatio(new BigDecimal(0.5));
                    //用户串关累计限额比例
                    rcsQuotaBusinessLimit.setUserStrayQuotaRatio(Constants.DAY_COMPENSATION_PROPORTION);

                    //用户串关单场限额 默认值为10万*20%
                    rcsQuotaBusinessLimit.setUserSingleStrayLimit(userSingleStrayLimit.longValue());

                    //冠军玩法商户限额比例
                    rcsQuotaBusinessLimit.setChampionBusinessProportion(Constants.DAY_COMPENSATION_PROPORTION);
                    //冠军玩法用户限额比例
                    rcsQuotaBusinessLimit.setChampionUserProportion(Constants.DAY_COMPENSATION_PROPORTION);
                    //货量百分比
                    rcsQuotaBusinessLimit.setBusinessBetPercent(Constants.BUSINESS_BET_PROPORTION);
                    String levelId = limitFlag == 1 ? "0" : "1";
                    rcsQuotaBusinessLimit.setTagMarketLevelId(levelId);
                    rcsQuotaBusinessLimit.setTagMarketLevelIdPc(levelId);
                    rcsQuotaBusinessLimit.setStatus(data.getStatus());
                    //商户渠道code
                    String parentName = data.getAgentLevel() == 0 ? "-" : data.getParentCode();
                    rcsQuotaBusinessLimit.setParentName(parentName);
                    //默认商户为新串关
                    rcsQuotaBusinessLimit.setStraySwitchVal(1);
                    int num = rcsQuotaBusinessLimitMapper.insert(rcsQuotaBusinessLimit);
                    if (num > 0) {
                        //初始化商户Rate数据,使用商户级通用设置
                        Map map = getAllRate();
                        RcsQuotaBusinessRateDTO dto = new RcsQuotaBusinessRateDTO();
                        dto.setBusinessId(Long.parseLong(data.getId()));
                        dto.setBusinessCode(data.getMerchantCode());
                        dto.setMtsRate(new BigDecimal(map.get("mtsRateAll").toString()));
                        dto.setVirtualRate(new BigDecimal(map.get("virtualRateAll").toString()));
                        dto.setVrEnable(Integer.parseInt(map.get("vrEnableAll").toString()));
                        dto.setCtsRate(new BigDecimal(map.get("ctsRateAll").toString()));
                        dto.setGtsRate(new BigDecimal(map.get("gtsRateAll").toString()));
                        dto.setOtsRate(new BigDecimal(map.get("otsRateAll").toString()));
                        dto.setRtsRate(new BigDecimal(map.get("rtsRateAll").toString()));
                        rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRate(dto);
                        this.sendMsg(rcsQuotaBusinessLimit);
                    }

                } else {
                    UpdateWrapper<RcsQuotaBusinessLimit> rcsQuotaBusinessLimitUpdateWrapper = new UpdateWrapper<>();
                    rcsQuotaBusinessLimitUpdateWrapper.lambda().eq(RcsQuotaBusinessLimit::getBusinessId, data.getId());
                    RcsQuotaBusinessLimit rcsQuotaBusinessLimit = new RcsQuotaBusinessLimit();
                    rcsQuotaBusinessLimit.setUserSingleStrayLimit(Objects.nonNull(rcsQuotaBusinessLimitData.getUserSingleStrayLimit()) ? rcsQuotaBusinessLimitData.getUserSingleStrayLimit() : userSingleStrayLimit.longValue());
                    rcsQuotaBusinessLimit.setBusinessName(data.getMerchantCode());
                    rcsQuotaBusinessLimit.setBusinessId(data.getId());
                    rcsQuotaBusinessLimit.setTagMarketLevelId(data.getTagMarketLevel());
                    rcsQuotaBusinessLimit.setStatus(data.getStatus());
                    int num = rcsQuotaBusinessLimitMapper.update(rcsQuotaBusinessLimit, rcsQuotaBusinessLimitUpdateWrapper);
                    if (num > 0) {
                        this.sendMsg(rcsQuotaBusinessLimit);
                    }
                }
                Map<String, Object> columnMap1 = new HashMap<>();
                columnMap1.put("value", data.getId());
                List<RcsCode> rcsCodes = rcsCodeMapper.selectByMap(columnMap1);
                if (CollectionUtils.isEmpty(rcsCodes)) {
                    RcsCode rcsCode = new RcsCode();
                    rcsCode.setFatherKey("business");
                    rcsCode.setChildKey(data.getMerchantCode());
                    rcsCode.setValue(data.getId());
                    rcsCode.setStatus(data.getStatus());
                    rcsCodeMapper.insert(rcsCode);
                } else {
                    rcsCodeMapper.updateRcsCode(data.getMerchantCode(), data.getId(), data.getStatus());
                }

                rcsCodeMapper.insertOrUpdateRcsMerchants(data.getId(), data.getMerchantCode(), data.getStatus(), limitFlag);
            }

        } catch (Exception e) {
            log.error("::{}::{},{}", "RDMIRG_" + data.getId(), e.getMessage(), e);
        }

        //新商户添加漏单设置
        addRcsOmitConfig(data);

    }

    private void addRcsOmitConfig(Merchant data){
        try {
            log.info("收到商户同步数据 添加漏单设置,id::{}::,data:{}", data.getId(), JSON.toJSONString(data));
            if(!StringUtils.isEmpty(data.getId()) && !StringUtils.isEmpty(data.getMerchantCode())){
                //新商户添加漏单设置
                RcsOmitConfig defaultSrc = rcsOmitConfigMapper.selectByMerchantId(999999999999L);
                if(Objects.isNull(defaultSrc)){
                    log.error("漏单设置 预设默认设置模版merchant_id:999,999,999,999, merchant_code:origin不存在");
                    return;
                }
                RcsOmitConfig rcsOmitConfigs = rcsOmitConfigMapper.selectByMerchantId(Long.valueOf(data.getId()));
                if(Objects.isNull(rcsOmitConfigs)){
                    RcsOmitConfig omitConfig = new RcsOmitConfig();
                    BeanUtils.copyProperties(defaultSrc, omitConfig);
                    omitConfig.setMerchantsId(Long.valueOf(data.getId()));
                    omitConfig.setMerchantsCode(data.getMerchantCode());
                    omitConfig.setIsDefaultSrc(2);
                    rcsOmitConfigMapper.insertEntity(omitConfig);

                    //按商户id缓存这个漏单设置
                    redisClient.hSet(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY,data.getId(), JSON.toJSONString(omitConfig));
                    redisClient.expireKey(REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY, 60 * 60 * 24 * 10);//TTL设置10天

                    //发送MQ redis更新消息
                    List<Long> merchantIds = new ArrayList<>();
                    merchantIds.add(Long.valueOf(data.getId()));
                    sendMessage(merchantIds);
                }
            }
            log.debug("添加漏单未设置 原因:getMerchantId, getMerchantCode为null");
        }catch (Exception e){
            log.error("::商户Id{} 新商户添加漏单设置 错误 ::{},{}", data.getId(), e.getMessage(), e);
        }
    }

    public void sendMessage(List<Long> merchantIds){
        //redis 缓存更新发送mq通知
        try{
            String key = "RcsMissedOrderConfigStatus";
            String linkId = key + "_" + System.currentTimeMillis();
            RcsMissedOrderConfigStatus obj = new RcsMissedOrderConfigStatus();
            obj.setStatus(1);
            obj.setMerchantIds(merchantIds);//配置更新之商户ID集合
            log.info("::::,发送MQ消息linkId={}, key={}", linkId, key);
            producerSendMessageUtils.sendMessage("RCS_RISK_REDIS_CACHE_UPDATE_SYNC_INFO", linkId, key, obj);
            log.info("::::RcsMissedOrderConfigStatus 发送MQ消息发送成功");
        }catch (Exception e){
            log.error("mq消息发送失败", e);
        }
    }


    private void sendMsg(RcsQuotaBusinessLimit rcsQuotaBusinessLimit) {
        RcsQuotaBusinessLimit rcsQuotaBusinessLimitNew = rcsQuotaBusinessLimitMapper.selectOne(new LambdaQueryWrapper<RcsQuotaBusinessLimit>().eq(RcsQuotaBusinessLimit::getBusinessId, rcsQuotaBusinessLimit.getBusinessId()));
        if (rcsQuotaBusinessLimitNew != null) {
            String key = String.format(RedisKey.MERCHANT_LIMIT_KEY, rcsQuotaBusinessLimit.getBusinessId());
            JSONObject json = new JSONObject();
            json.put("key", key);
            json.put("value", rcsQuotaBusinessLimitNew);
            producerSendMessageUtils.sendMessage("rcs_order_limit_cache_update", "", key, json);
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setConsumeThreadMin(64);
        consumer.setConsumeThreadMax(128);
    }

    public Map getAllRate(){
        Map<String,String> rateMap = new HashMap();
        String mtsRateAll = redisClient.get(Constants.MTS_AMOUNT_RATE_ALL);
        String virtualRateAll = redisClient.get(Constants.VIRTUAL_AMOUNT_RATE_ALL);
        String vrEnableAll = redisClient.get(Constants.VR_ENABLE_AMOUNT_RATE_ALL);
        String ctsRateAll = redisClient.get(Constants.CTS_AMOUNT_RATE_ALL);
        String gtsRateAll = redisClient.get(Constants.GTS_AMOUNT_RATE_ALL);
        String otsRateAll = redisClient.get(Constants.OTS_AMOUNT_RATE_ALL);
        String rtsRateAll = redisClient.get(Constants.RTS_AMOUNT_RATE_ALL);
        if(mtsRateAll == null){
            redisClient.set(Constants.MTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            mtsRateAll = redisClient.get(Constants.MTS_AMOUNT_RATE_ALL);
        }
        if(virtualRateAll == null){
            redisClient.set(Constants.VIRTUAL_AMOUNT_RATE_ALL,new BigDecimal(1));
            virtualRateAll = redisClient.get(Constants.VIRTUAL_AMOUNT_RATE_ALL);
        }
        if(vrEnableAll == null){
            redisClient.set(Constants.VR_ENABLE_AMOUNT_RATE_ALL,1);
            vrEnableAll = redisClient.get(Constants.VR_ENABLE_AMOUNT_RATE_ALL);
        }

        if(ctsRateAll == null){
            redisClient.set(Constants.CTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            ctsRateAll = redisClient.get(Constants.CTS_AMOUNT_RATE_ALL);
        }
        if(gtsRateAll == null){
            redisClient.set(Constants.GTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            gtsRateAll = redisClient.get(Constants.GTS_AMOUNT_RATE_ALL);
        }
        if(otsRateAll == null){
            redisClient.set(Constants.OTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            otsRateAll = redisClient.get(Constants.OTS_AMOUNT_RATE_ALL);
        }
        if(rtsRateAll == null){
            redisClient.set(Constants.RTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            rtsRateAll = redisClient.get(Constants.RTS_AMOUNT_RATE_ALL);
        }

        rateMap.put("mtsRateAll",mtsRateAll);//mts通用折扣利率
        rateMap.put("virtualRateAll",virtualRateAll);//虚拟通用折扣利率
        rateMap.put("vrEnableAll",vrEnableAll);//虚拟通用折扣利率

        rateMap.put("ctsRateAll",ctsRateAll);//mts通用折扣利率
        rateMap.put("gtsRateAll",gtsRateAll);//虚拟通用折扣利率
        rateMap.put("otsRateAll",otsRateAll);//虚拟通用折扣利率
        rateMap.put("rtsRateAll",rtsRateAll);//虚拟通用折扣利率
        return rateMap;
    }
}
