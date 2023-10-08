package com.panda.sport.rcs.trade.wrapper.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsMerchantCommonConfigMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsMerchantCommonConfig;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.trade.enums.SportTypeEnum;
import com.panda.sport.rcs.trade.util.CommonUtil;
import com.panda.sport.rcs.trade.wrapper.IRcsMerchantCommonConfigService;
import com.panda.sport.rcs.utils.TradeUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户动态风控全局开关配置
 *
 * @description:
 * @author: magic
 * @create: 2022-05-15 11:15
 **/
@Slf4j
@Service
public class RcsMerchantCommonConfigServiceImpl extends ServiceImpl<RcsMerchantCommonConfigMapper, RcsMerchantCommonConfig> implements IRcsMerchantCommonConfigService {

    @Autowired
    ProducerSendMessageUtils sendMessage;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    public static final String logCode = "10043";
    private final String logTitle = "动态风控设置";
    private final String isYes = "开";
    private final String isNo = "关";
    private final String MARKET_LEVEL_NAME = "赔率分组动态风控";
    private final String MARKET_BET_NAME = "投注货量动态风控-赛种";
    private final String OBJECT_NAME = "通用设置";

    @Override
    @Transactional
    public void update(RcsMerchantCommonConfig rcsMerchantCommonConfig, int traderId) {
        List<RcsMerchantCommonConfig> list = baseMapper.selectList(new LambdaQueryWrapper<>());
        if (CollectionUtils.isEmpty(list)) {
            throw new RcsServiceException("商户动态风控全局开关配置未初始化");
        }
        RcsMerchantCommonConfig rcsMerchantCommonConfigReqVo = list.get(0);
        RcsMerchantCommonConfig o = new RcsMerchantCommonConfig();
        BeanUtils.copyProperties(rcsMerchantCommonConfigReqVo, o);
        log.info("::{}::操作员：{}，修改商户动态风控全局开关配置:{},历史配置:{}", CommonUtil.getRequestId(), traderId, JSONObject.toJSONString(rcsMerchantCommonConfig), JSONObject.toJSONString(o));
        o.setBetLimitStatus(rcsMerchantCommonConfig.getBetLimitStatus());
        o.setTagMarketLevelStatus(rcsMerchantCommonConfig.getTagMarketLevelStatus());
        o.setPreSettlementStatus(rcsMerchantCommonConfig.getPreSettlementStatus());
        o.setBetDelayStatus(rcsMerchantCommonConfig.getBetDelayStatus());
        o.setBetVolumeStatus(rcsMerchantCommonConfig.getBetVolumeStatus());
        o.setSportIds(rcsMerchantCommonConfig.getSportIds());
        o.setUpdateTime(new Date());
        baseMapper.updateById(o);
        sendMerchantCommonConfig(o);
        setBusinessLog(rcsMerchantCommonConfig,rcsMerchantCommonConfigReqVo);
    }

    /**
     *
     * */
    private void setBusinessLog(RcsMerchantCommonConfig news,RcsMerchantCommonConfig old){
        List<RcsQuotaBusinessLimitLog> limitLogList=new ArrayList<>();
        try {
            if (!news.getTagMarketLevelStatus().equals(old.getTagMarketLevelStatus())) {
                String beforeVal = old.getTagMarketLevelStatus() == 0 ? isNo : isYes;
                String afterVal = news.getTagMarketLevelStatus() == 0 ? isNo : isYes;
                limitLogList.add(setMerchantCommonConfig("", OBJECT_NAME, MARKET_LEVEL_NAME, beforeVal, afterVal));
            }
            if (!news.getBetVolumeStatus().equals(old.getBetVolumeStatus())) {
                String beforeVal = old.getBetVolumeStatus() == 0 ? isNo : isYes;
                String afterVal = news.getBetVolumeStatus() == 0 ? isNo : isYes;
                if (old.getBetVolumeStatus() != 0) {
                    if (StringUtils.isNotEmpty(old.getSportIds())) {
                        beforeVal = beforeVal + "-" + Arrays.stream(old.getSportIds().split(",")).map(Integer::valueOf).map(SportTypeEnum::getValue).collect(Collectors.joining(","));
                    }
                }
                if (news.getBetVolumeStatus() != 0) {
                    if (StringUtils.isNotEmpty(news.getSportIds())) {
                        afterVal = afterVal + "-" + Arrays.stream(news.getSportIds().split(",")).map(Integer::valueOf).map(SportTypeEnum::getValue).collect(Collectors.joining(","));
                    }
                }
                limitLogList.add(setMerchantCommonConfig("", OBJECT_NAME, MARKET_BET_NAME, beforeVal, afterVal));
            } else if (!news.getSportIds().equals(old.getSportIds())) {
                String beforeVal = "";
                String afterVal = "";
                if (StringUtils.isNotEmpty(old.getSportIds())) {
                    beforeVal = old.getBetVolumeStatus() == 0 ? isNo : isYes;
                    if (old.getBetVolumeStatus() != 0) {
                        beforeVal = beforeVal + "-" + Arrays.stream(old.getSportIds().split(",")).map(Integer::valueOf).map(SportTypeEnum::getValue).collect(Collectors.joining(","));
                    }
                }
                if (!CollectionUtils.isEmpty(news.getSportIdList())) {
                    afterVal = news.getBetVolumeStatus() == 0 ? isNo : isYes;
                    if (news.getBetVolumeStatus() != 0) {
                        afterVal = afterVal + "-" + news.getSportIdList().stream().map(Integer::valueOf).map(SportTypeEnum::getValue).collect(Collectors.joining(","));
                    }
                }
                limitLogList.add(setMerchantCommonConfig("", OBJECT_NAME, MARKET_BET_NAME, beforeVal, afterVal));
            }
            if(!CollectionUtils.isEmpty(limitLogList)){
                //每条数据中添加IP地址
                for(RcsQuotaBusinessLimitLog iplog: limitLogList){
                    iplog.setIp(news.getIp());
                }
                String arrString = JSONArray.toJSONString(limitLogList);
                log.info("{}->处理条数{}",logTitle,limitLogList.size());
                producerSendMessageUtils.sendMessage(CommonUtil.RCS_BUSINESS_LOG_SAVE,null,logCode,arrString);
            }
        }catch (Exception ex){
           log.error("发送日志错误{}",ex.getMessage(),ex);
        }
    }
    private RcsQuotaBusinessLimitLog setMerchantCommonConfig(String objectId,String object_name, String paramName,
                                                   String beforeVal, String afterVal) throws Exception {

        String userId= TradeUserUtils.getUserId().toString();
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(null);
        limitLoglog.setObjectName(OBJECT_NAME);
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(logCode);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(userId);
        //limitLoglog.setUserName(linkedHashMap.get("userName").toString());
        return limitLoglog;
    }
    /**
     * 给业务发生mq
     *
     * @param rcsMerchantCommonConfig
     */
    private void sendMerchantCommonConfig(RcsMerchantCommonConfig rcsMerchantCommonConfig) {
        if (rcsMerchantCommonConfig!=null) {
            sendMessage.sendMessage("rcs_merchant_common_config", rcsMerchantCommonConfig);
        }
    }
}
