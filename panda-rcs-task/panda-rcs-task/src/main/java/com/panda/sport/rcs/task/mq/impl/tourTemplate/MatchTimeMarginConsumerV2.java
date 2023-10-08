package com.panda.sport.rcs.task.mq.impl.tourTemplate;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.dto.MarketMarginDtlDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.TradeMarketMarginConfigDTO;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.Basketball;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainRefMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsCategoryOddTemplet;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.odds.MatchMarketPlaceConfig;
import com.panda.sport.rcs.pojo.odds.MatchOddsConfig;
import com.panda.sport.rcs.pojo.odds.MatchPlayConfig;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.task.wrapper.impl.RcsMatchMarketConfigServiceImpl;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.utils.i18n.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project Name : panda-rcs-task
 * @Package Name : panda-rcs-task
 * @Description : 分时margin
 * @Author : Paca
 * @Date : 2021-03-16 20:50
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_TIME_MARGAIN",
        consumerGroup = "rcs_task_RCS_TIME_MARGAIN",
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
public class MatchTimeMarginConsumerV2 implements RocketMQListener<MatchEventInfoMessage> {

    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private ITradeMarketConfigApi tradeMarketConfigApi;

    @Autowired
    private RcsTournamentTemplatePlayMargainMapper playMarginMapper;

    @Autowired
    private RcsTournamentTemplatePlayMargainRefMapper playMarginRefMapper;

    @Autowired
    private RcsMatchMarketConfigServiceImpl rcsMatchMarketConfigServiceImpl;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    private static Long SNOOKER_TIMEOUT_EVENT_TIME = 3600 * 24 * 7L;

    @Override
    public void onMessage(MatchEventInfoMessage event) {
        try {
            handleTimeOutMargin(event);
        } catch (Exception e) {
            log.error("处理暂停分时margin异常", e);
        }
    }


    private void handleTimeOutMargin(MatchEventInfoMessage event) {
        log.info("进入RCS_TIME_MARGAIN,参数为{}", JSONObject.toJSONString(event));
        String eventCode = event.getEventCode();
        Long matchId = event.getStandardMatchId();
        if (!SportIdEnum.isBasketball(event.getSportId()) && !SportIdEnum.isSnooker(event.getSportId())) {
            return;
        }
        if (!"timeout_over".equalsIgnoreCase(eventCode) && !"timeout".equalsIgnoreCase(eventCode)) {
            log.warn("不是timeout_over和timeout事件不处理：matchId={},eventCode={}", matchId, eventCode);
            return;
        }
        String redisKey = String.format("rcs:task:match:event:%s", matchId);

        List<RcsMatchMarketConfig> playPlaceConfigList = new ArrayList<>();
        if(SportIdEnum.isSnooker(event.getSportId())){
            playPlaceConfigList = playMarginMapper.queryPlaceConfigByMatchIdSnooker(matchId, 0);
            redisClient.setExpiry(redisKey, eventCode, SNOOKER_TIMEOUT_EVENT_TIME);
        }else{
            redisClient.setExpiry(redisKey, eventCode, 600L);
            playPlaceConfigList = playMarginMapper.queryPlaceConfigByMatchId(matchId, 0);

            //篮球带X玩法也全部查出来
            List<RcsMatchMarketConfig> playPlaceConfigList1 = playMarginMapper.queryPlaceConfigByMatchIdSnooker(matchId, 0);
            playPlaceConfigList.addAll(playPlaceConfigList1);
        }

        if (CollectionUtils.isEmpty(playPlaceConfigList)) {
            log.warn("未查询到位置配置：matchId={},eventCode={}", matchId, eventCode);
            return;
        }

        MatchOddsConfig matchConfig = new MatchOddsConfig();
        matchConfig.setMatchId(String.valueOf(matchId));
        matchConfig.setPlayConfigList(Lists.newArrayList());

        List<TradeMarketMarginConfigDTO> apiConfig = new ArrayList<>();
        //子玩法不等于-1的
        List<TradeMarketMarginConfigDTO> apiConfigForXPlayList = new ArrayList<>();
        Map<Long, List<RcsMatchMarketConfig>> playConfigMap = playPlaceConfigList.stream().collect(Collectors.groupingBy(RcsMatchMarketConfig::getPlayId));
        playConfigMap.forEach((playId, placeConfigList) -> {
            if (!checkPlaceConfig(placeConfigList)) {
                return;
            }
            RcsTournamentTemplatePlayMargain playTemplateConfig = getPlayTemplateConfig(matchId, playId.intValue());
            if (playTemplateConfig == null) {
                log.warn("玩法模板配置为空：matchId={},playId={}", matchId, playId);
                return;
            }
            MatchPlayConfig playConfig = new MatchPlayConfig();
            playConfig.setMarketType(playTemplateConfig.getMarketType());
            playConfig.setPlayId(playId.toString());
            playConfig.setRcsTournamentTemplatePlayMargain(playTemplateConfig);
            playConfig.setPlaceConfig(Lists.newArrayList());
            playConfig.setPlaceSpreadMap(Maps.newHashMap());
            matchConfig.getPlayConfigList().add(playConfig);

            List<RcsCategoryOddTemplet> oddsList = rcsMatchMarketConfigServiceImpl.getTemplate(matchId, playId.intValue());
            placeConfigList.forEach(marketBean -> {
                Integer placeNum = marketBean.getMarketIndex();
                BigDecimal margin = "timeout".equalsIgnoreCase(eventCode) ? marketBean.getTimeOutMargin() : marketBean.getMargin();
                if (Basketball.Main.isHandicapOrTotal(playId)) {
                    playConfig.getPlaceSpreadMap().put(placeNum, margin);
                } else {
                    MatchMarketPlaceConfig placeConfig = new MatchMarketPlaceConfig();
                    placeConfig.setPlaceNum(placeNum);
                    placeConfig.setSpread(margin.toPlainString());
                    playConfig.getPlaceConfig().add(placeConfig);
                }
                if (NumberUtils.INTEGER_ONE.equals(placeNum)) {
                    playTemplateConfig.setMargain(margin.toPlainString());
                }

                TradeMarketMarginConfigDTO marginDto = new TradeMarketMarginConfigDTO();
                marginDto.setPlaceNum(placeNum);
                marginDto.setStandardCategoryId(playId);
                marginDto.setStandardMatchInfoId(matchId);
                marginDto.setMarketType(0);
                List<MarketMarginDtlDTO> marginList = rcsMatchMarketConfigServiceImpl.buildMargainList(margin, playId.intValue(), oddsList, playConfig.getMarketType());
                marginDto.setMarketMarginDtlDTOList(marginList);
                apiConfig.add(marginDto);

                if(StringUtils.isNotBlank(marketBean.getSubPlayId()) && !marketBean.getSubPlayId().equalsIgnoreCase("-1")){
                    marginDto.getMarketMarginDtlDTOList().forEach(e -> e.setChildStandardCategoryId(Long.parseLong(marketBean.getSubPlayId())));
                    apiConfigForXPlayList.add(marginDto);
                }
            });
        });

        if (CollectionUtils.isEmpty(apiConfig)) {
            log.warn("没有需要下发的margin，不处理：matchId={},eventCode={}", matchId, eventCode);
            return;
        }

        String linkId = CommonUtils.getLinkId("task");
        matchConfig.setMessageSource(eventCode);
        matchConfig.setLinkId(linkId);
        // 发送到操盘统一计算，手动模式需要操盘自己计算
        producerSendMessageUtils.sendMessage("RCS_TRADE_MATCH_ODDS_CONFIG", matchId + "_" + eventCode, linkId, matchConfig);

        DataRealtimeApiUtils.handleApi(apiConfig, Long.toString(matchId), 0, new DataRealtimeApiUtils.ApiCall() {
            @Override
            public <R> Response<R> callApi(Request request) {
                request.setLinkId(request.getLinkId() + "_" + eventCode);
                return tradeMarketConfigApi.putTradeMarketMarginConfigList(request);
            }
        });
       try {
           Thread.sleep(500);
       }catch (Exception e){
           log.error("线程休眠错误");
       }
        if(CollectionUtils.isNotEmpty(apiConfigForXPlayList)){
            //子玩法不等于-1的数据单独调用一次
            DataRealtimeApiUtils.handleApi(apiConfigForXPlayList, Long.toString(matchId), -1, new DataRealtimeApiUtils.ApiCall() {
                @Override
                public <R> Response<R> callApi(Request request) {
                    request.setLinkId(request.getLinkId() + "_x_" + eventCode);
                    return tradeMarketConfigApi.putTradeMarketMarginConfigList(request);
                }
            });
        }
    }

    private boolean checkPlaceConfig(List<RcsMatchMarketConfig> placeConfigList) {
        boolean existMain = false;
        boolean existMarginNotEqual = false;
        for (RcsMatchMarketConfig config : placeConfigList) {
            if (NumberUtils.INTEGER_ONE.equals(config.getMarketIndex())) {
                existMain = true;
            }
            if (config.getTimeOutMargin() == null) {
                config.setTimeOutMargin(config.getMargin());
            }
            if (config.getMargin().compareTo(config.getTimeOutMargin()) != 0) {
                existMarginNotEqual = true;
            }
        }
        if (existMain && existMarginNotEqual) {
            return true;
        } else {
            log.warn("配置检查：existMain={},existMarginNotEqual={}", existMain, existMarginNotEqual);
            return false;
        }
    }

    private RcsTournamentTemplatePlayMargain getPlayTemplateConfig(Long matchId, Integer playId) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("matchId", matchId);
        // 滚球
        map.put("matchType", 0);
        map.put("playId", playId);
        return playMarginRefMapper.queryMatchMargainInfo(map);
    }

}
