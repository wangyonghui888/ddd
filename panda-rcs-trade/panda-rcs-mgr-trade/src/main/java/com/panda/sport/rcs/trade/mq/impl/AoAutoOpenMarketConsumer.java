package com.panda.sport.rcs.trade.mq.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.pojo.param.AutoOpenMarketStatusParam;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.trade.enums.PeriodEnum;
import com.panda.sport.rcs.trade.mq.RcsConsumer;
import com.panda.sport.rcs.trade.service.RcsSpecEventConfigService;
import com.panda.sport.rcs.trade.wrapper.SportMatchViewService;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsTournamentTemplatePlayMargainService;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsTournamentTemplateService;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.RedisKey.getPlaySetCodeStatusKey;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "RCS_AO_AUTO_OPEN_MARKET_TOPIC",
        consumerGroup = "RCS_AO_AUTO_OPEN_MARKET_TOPIC_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class AoAutoOpenMarketConsumer extends RcsConsumer<String> {

    @Autowired
    private RcsSpecEventConfigService rcsSpecEventConfigService;

    @Autowired
    protected ProducerSendMessageUtils producerSendMessageUtils;

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private SportMatchViewService sportMatchViewService;
    @Autowired
    private IRcsTournamentTemplateService tournamentTemplateService;
    @Resource
    private IRcsTournamentTemplatePlayMargainService tournamentTemplatePlayMargainService;

    @Override
    protected String getTopic() {
        return "RCS_AO_AUTO_OPEN_MARKET_TOPIC";
    }

    @Override
    protected Boolean handleMs(String message) {
        if (StringUtils.isBlank(message)) {
            return true;
        }
        JSONObject jsonObject = JSON.parseObject(message);
        String matchId = jsonObject.getString("matchId");

        /*String authSwitchKey = String.format(RedisKey.AO_PLAY_AUTO_OPEN, 0, 100l);
        String sysSwitch = redisUtils.get(authSwitchKey);
        if(StringUtils.isBlank(sysSwitch) || "0".equals(sysSwitch)){
            log.info("{}::AO自动开盘::系统级开关未开::{}",matchId,sysSwitch);
            return true;
        }*/

        AutoOpenMarketStatusParam autoOpenMarketStatusParam = new AutoOpenMarketStatusParam();
        autoOpenMarketStatusParam.setSwitchType(0);
        HttpResponse<Integer> autoOpenMarketStatus = rcsSpecEventConfigService.getAutoOpenMarketStatus(autoOpenMarketStatusParam);
        Integer sysSwitch = autoOpenMarketStatus.getData();
        if(null == sysSwitch || 0==sysSwitch){
            log.info("{}::AO自动开盘::系统级开关未开::{}",matchId,sysSwitch);
            return true;
        }

        autoOpenMarketStatusParam.setSwitchType(1);
        autoOpenMarketStatusParam.setTypeVal(Long.valueOf(matchId));
        autoOpenMarketStatusParam.setType(3);
        autoOpenMarketStatus = rcsSpecEventConfigService.getAutoOpenMarketStatus(autoOpenMarketStatusParam);
        Integer switchStr = autoOpenMarketStatus.getData();

        /*String specEventStatusKey = String.format(RedisKey.AO_PLAY_AUTO_OPEN, 3, matchId);
        String switchStr = redisUtils.get(specEventStatusKey);*/
        if(null == switchStr || 0==switchStr){//赛事级开关未开
            log.info("{}::AO自动开盘::赛事级开关未开::{}",matchId,switchStr);
            return true;
        }
        Map<String, String> map = new HashMap<>();
        String linkId = CommonUtils.getLinkId() + "_ws";
        map.put("linkId", linkId);
        map.put("matchId", matchId);
        if(!aoOpen(linkId,Long.valueOf(matchId))){
            return true;
        }
        //发送ws通知：是否AO玩法自动开盘
        producerSendMessageUtils.sendMessage("RCS_AO_AUTO_OPEN_MARKET_STATUS", "RCS_AO_AUTO_OPEN_MARKET_STATUS_GROUP", linkId, map);
        return true;
    }


    /**
     * 获取赛事玩法集状态
     * */
    private Integer getPlaySetCodeStatus(Long matchId) {
        String key = getPlaySetCodeStatusKey(matchId);
        String value = redisUtils.hget(key, PeriodEnum.FULL_TIME_1.getPlaySetCode());
        return NumberUtils.toInt(value, TradeStatusEnum.OPEN.getStatus());
    }

    /**
     * 如果赛事是锁盘或者封盘状态，查询赛事所有的AO玩法ID
     */
    private boolean aoOpen(String linkId, Long matchId) {

        MatchMarketLiveBean matchMarketLiveBean = sportMatchViewService.queryByMatchId(matchId, linkId);
        if (matchMarketLiveBean == null) {
            log.info("{}::{}::AO自动开盘::未查询到赛事", linkId, matchId);
            return false;
        }
        //这里要判断是不是封盘
        /*5-12 产品确认修改为判断赛事玩法集状态
       if(matchMarketLiveBean.getOperateMatchStatus() == null || matchMarketLiveBean.getOperateMatchStatus() != 1 ){
            log.info("{}::{}::AO自动开盘::赛事不是封盘状态::=>{}", linkId, matchId, JSONUtil.toJsonStr(matchMarketLiveBean));
            return false;
        }*/

        Integer playSetCodeStatus = getPlaySetCodeStatus(matchId);
        if(null == playSetCodeStatus || playSetCodeStatus != 1 ){
            log.info("{}::{}::AO自动开盘::赛事玩法集不是封盘状态::=>{}", linkId, matchId, playSetCodeStatus);
            return false;
        }
        RcsTournamentTemplate tournamentTemplate = tournamentTemplateService.queryByMatchId(matchId,0);
        if(tournamentTemplate == null){
            log.info("{}::{}::AO自动开盘::未查询到赛事模板", linkId, matchId);
            return false;
        }

        List<RcsTournamentTemplatePlayMargain> playMargainList = tournamentTemplatePlayMargainService.queryByTemplateId(tournamentTemplate.getId());
        if(CollUtil.isEmpty(playMargainList)){
            log.info("{}::{}::AO自动开盘::未查询到赛事玩法", linkId, matchId);
            return false;
        }
        List<RcsTournamentTemplatePlayMargain> aoMargainList = playMargainList.stream().filter(o->"AO".equals(o.getDataSource())).collect(Collectors.toList());
        if(CollUtil.isEmpty(aoMargainList)){
            log.info("{}::{}::AO自动开盘::赛事没有AO玩法", linkId, matchId);
            return false;
        }
        List<RcsTournamentTemplatePlayMargain> otherMargainList= playMargainList.stream().filter(o->!"AO".equals(o.getDataSource())).collect(Collectors.toList());
        //把赛事玩法id存入缓存,等待前端确认操作
        List<Integer> aoPlayIds = aoMargainList.stream().map(RcsTournamentTemplatePlayMargain::getPlayId).collect(Collectors.toList());
        List<Integer> otherPlayIds = otherMargainList.stream().map(RcsTournamentTemplatePlayMargain::getPlayId).collect(Collectors.toList());
        String dataKey = String.format(RedisKey.AO_PLAY_AUTO_OPEN_DATA, matchId);
        Map<String,Object> map = new HashMap<>();
        map.put("aoPlayIds",aoPlayIds);
        map.put("otherPlayIds",otherPlayIds);
        redisUtils.setex(dataKey, JSON.toJSONString(map), 10 * 60, TimeUnit.SECONDS);
        log.info("{}::{}::AO自动开盘::赛事AO玩法数据缓存", linkId, matchId);
        return true;
    }
}
