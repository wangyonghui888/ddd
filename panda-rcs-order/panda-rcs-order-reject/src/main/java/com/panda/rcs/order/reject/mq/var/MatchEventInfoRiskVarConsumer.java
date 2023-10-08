package com.panda.rcs.order.reject.mq.var;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.Request;
import com.panda.rcs.order.reject.constants.NumberConstant;
import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.rcs.order.reject.entity.enums.VarSwitchEnum;
import com.panda.rcs.order.reject.service.MatchInfoService;
import com.panda.rcs.order.reject.service.RejectTemplateAcceptConfigServer;
import com.panda.rcs.order.reject.utils.SendMessageUtils;
import com.panda.sport.data.rcs.vo.MatchEventInfo;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.enums.OrderStatusEnum;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.enums.MatchEventConfigEnum;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 2576VAR事件处理
 *
 * @author eamon
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MqConstants.MATCH_EVENT_INFO_TO_RISK,
        consumerGroup = "rcs_reject_event_info_var_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchEventInfoRiskVarConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Resource
    StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    MatchInfoService matchInfoService;
    @Autowired
    RejectTemplateAcceptConfigServer rejectTemplateAcceptConfigServer;

    @Autowired
    SendMessageUtils sendMessage;
    private static final List<String> VAR_EVENT_LIST = Arrays.asList("possible_var",
            "possible_video_assistant_referee", "var_reason", "var_reviewing", "video_assistant_referee");
    private static final List<String> END_VAR_EVENT_LIST = Arrays.asList("video_assistant_referee_over",
            "canceled_video_assistant_referee");
    private static final List<String> POSSIBLE_EVENT_LIST = Arrays.asList("possible_goal", "possible_red_card",
            "possible_penalty");
    private static final List<String> CANCELED_EVENT_LIST = Arrays.asList("canceled_goal", "canceled_red_card",
            "canceled_penalty");
    private static final List<String> REJECT_EVENT_LIST = Arrays.asList("reject_event");
    private static final List<String> SAFE_EVENT_LIST = Arrays.asList("ball_safe");
    private static final List<String> EVENT_SOURCE_LIST = Arrays.asList(
            MatchEventConfigEnum.EVENT_SOURCE_SR.getCode(), MatchEventConfigEnum.EVENT_SOURCE_BC.getCode(),
            MatchEventConfigEnum.EVENT_SOURCE_BG.getCode(), MatchEventConfigEnum.EVENT_SOURCE_RB.getCode(),
            MatchEventConfigEnum.EVENT_SOURCE_KO.getCode(), MatchEventConfigEnum.EVENT_SOURCE_KO.getCode(),
            MatchEventConfigEnum.EVENT_SOURCE_PD.getCode(), MatchEventConfigEnum.EVENT_SOURCE_PD_TWO.getCode());


    //缓存赛事信息
    private static Map<String, StandardMatchInfo> matchCache = new HashMap<>(1000);


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMqPushConsumer) {
        defaultMqPushConsumer.setConsumeThreadMin(64);
        defaultMqPushConsumer.setConsumeThreadMax(126);
    }

    @Override
    public void onMessage(String message) {
        String linkId = "nullStr";
        Long matchId = Long.parseLong("-1");
        try {
            dealVarEvent(message);
            Request<List<MatchEventInfo>> requests = JSON.parseObject(message, new TypeReference<Request<List<MatchEventInfo>>>() {
            });
            linkId = requests.getLinkId();
            List<MatchEventInfo> data = requests.getData();
            matchId = data.get(NumberConstant.NUM_ZERO).getStandardMatchId();
            sendMessage.sendMessage("MATCH_EVENT_INFO_TO_REJECT", matchId.toString(), linkId, JSONObject.parseObject(message));
        } catch (Exception e) {
            log.error("linkId::{}::,赛事ID:{},rcs_reject_event_info_var_group消费MATCH_EVENT_INFO_TO_RISK异常,参数={},", matchId, linkId, JSONObject.parseObject(message), e);
        }
    }


    public void dealVarEvent(String message) {
        String linkId = "nullStr";
        Long matchId = Long.parseLong("-1");
        try {
            Request<List<MatchEventInfo>> requests = JSON.parseObject(message, new TypeReference<Request<List<MatchEventInfo>>>() {
            });
            linkId = requests.getLinkId();
            List<MatchEventInfo> data = requests.getData();
            matchId = data.get(NumberConstant.NUM_ZERO).getStandardMatchId();
            Long sportId = data.get(NumberConstant.NUM_ZERO).getSportId();
            if (!CollectionUtils.isEmpty(data)) {
                if (sportId != NumberConstant.NUM_ONE) {
                    log.info("linkId::{}::,赛事ID:{},不是足球事件", linkId, matchId);
                    return;
                }
                for (MatchEventInfo matchEventInfo : data) {
                    if (EVENT_SOURCE_LIST.contains(matchEventInfo.getDataSourceCode())) {
                        StandardMatchInfo match = getMatchInfo(String.valueOf(matchId));
                        if (match == null) {
                            log.warn("linkId::{}::,赛事ID:{},不存在标准赛事表里", linkId, matchId);
                            return;
                        }
                        //不是赛事数据源的事件，不进入逻辑
                        if (!matchEventInfo.getDataSourceCode().equalsIgnoreCase(rejectTemplateAcceptConfigServer.getDataSourceCode("131", matchId))) {
                            return;
                        }
                        //如果VAR收单开关状态为关闭
                        if (!matchInfoService.getVarSwitchStatus(String.valueOf(matchEventInfo.getStandardMatchId()))) {
                            return;
                        }
                        if (VAR_EVENT_LIST.contains(matchEventInfo.getEventCode())) {
                            //当前事件为VAR事件
                            //修改缓存var收单状态为true
                            matchInfoService.updateVarAccept(String.valueOf(matchId), VarSwitchEnum.Open.getCode());
                            //发送延时600S的MQ：RCS_VAR_MATCH_CLOSE
                            JSONObject json = new JSONObject()
                                    .fluentPut("matchId", matchId)
                                    .fluentPut("sportId", 1)
                                    .fluentPut("linkId", linkId);
                            String topic = "RCS_VAR_MATCH_CLOSE";
                            String tags = matchId + "_" + sportId + "_VAR_600S_CLOSE";
                            String keys = matchId + "_" + sportId;
                            log.info("::{}::赛事ID:{},当前事件为:{},发送VAR收单超600S赛事级封盘消息队列topic={},tags={}",
                                    linkId, matchId, matchEventInfo.getEventCode(), topic, tags);
                            sendMessage.sendMessage(topic, tags, keys, json, null, 14);
                        } else if (END_VAR_EVENT_LIST.contains(matchEventInfo.getEventCode())) {
                            //当前事件为VAR结束/VAR取消
                            //判断var收单状态:rcs:order:var:accept:=1
                            if (VarSwitchEnum.Open.getCode().equalsIgnoreCase(matchInfoService.getVarAccept(String.valueOf(matchId)))) {
                                //修改缓存var收单状态为false
                                matchInfoService.updateVarAccept(String.valueOf(matchId), VarSwitchEnum.Close.getCode());
                                //VAR订单发送等待mq：RCS_VAR_ORDER_REJECT
                                matchInfoService.sendVarOrderStatus(linkId, String.valueOf(matchId), String.valueOf(sportId),
                                        OrderStatusEnum.ORDER_WAITING.getCode());
                                log.info("::{}::赛事ID:{},当前事件为:{},VAR取消退出VAR收单", linkId, matchId, matchEventInfo.getEventCode());

                            }
                        } else if (CANCELED_EVENT_LIST.contains(matchEventInfo.getEventCode())) {
                            //当前事件为取消进球/取消红牌/点球取消
                            //上一个事件为可能进球/可能红牌/可能点球
                            String lastEventKey = String.format(RedisKey.REDIS_EVENT_INFO, matchEventInfo.getDataSourceCode(), matchEventInfo.getStandardMatchId());
                            MatchEventInfo lastEventValue = JSON.parseObject(RcsLocalCacheUtils.getValueInfo(lastEventKey), MatchEventInfo.class);
                            String lastEventCode = Objects.nonNull(lastEventValue) ? lastEventValue.getEventCode() : "";
                            String lastEventKey1 = String.format(RedisKey.MATCH_LAST_TIME_EVENT_CODE, matchEventInfo.getDataSourceCode(), matchEventInfo.getStandardMatchId());
                            MatchEventInfo lastEventValue1 = JSON.parseObject(RcsLocalCacheUtils.getValueInfo(lastEventKey1), MatchEventInfo.class);
                            String lastEventCode1 = Objects.nonNull(lastEventValue1) ? lastEventValue1.getEventCode() : "";
                            log.info("::{}::赛事ID:{},当前事件为:{},上一次事件为:{},上上一次事件为:{}", linkId, matchId, matchEventInfo.getEventCode(), lastEventCode, lastEventCode1);

                            if (POSSIBLE_EVENT_LIST.contains(lastEventCode) || POSSIBLE_EVENT_LIST.contains(lastEventCode1)) {
                                //判断var收单状态:rcs:order:var:accept:=1
                                if (VarSwitchEnum.Open.getCode().equalsIgnoreCase(matchInfoService.getVarAccept(String.valueOf(matchId)))) {
                                    //修改缓存var收单状态为false
                                    matchInfoService.updateVarAccept(String.valueOf(matchId), VarSwitchEnum.Close.getCode());
                                    //VAR订单发送等待mq：RCS_VAR_ORDER_REJECT
                                    matchInfoService.sendVarOrderStatus(linkId, String.valueOf(matchId), String.valueOf(sportId),
                                            OrderStatusEnum.ORDER_WAITING.getCode());
                                    log.info("::{}::赛事ID:{},当前事件为:{},上一次事件为:{},取消可能进球退出VAR收单", linkId, matchId, matchEventInfo.getEventCode(), lastEventValue);
                                }
                            }
                        } else if (REJECT_EVENT_LIST.contains(matchEventInfo.getEventCode())) {
                            //当前事件为reject_event拒单事件
                            //判断var收单状态:rcs:order:var:accept:=1
                            if (VarSwitchEnum.Open.getCode().equalsIgnoreCase(matchInfoService.getVarAccept(String.valueOf(matchId)))) {
                                //修改缓存var收单状态为false
                                matchInfoService.updateVarAccept(String.valueOf(matchId), VarSwitchEnum.Close.getCode());
                                //VAR订单发送等待mq：RCS_VAR_ORDER_REJECT
                                matchInfoService.sendVarOrderStatus(linkId, String.valueOf(matchId), String.valueOf(sportId),
                                        OrderStatusEnum.ORDER_REJECT.getCode());
                                log.info("::{}::赛事ID:{},当前事件为:{},拒单事件退出VAR收单", linkId, matchId, matchEventInfo.getEventCode());
                            }
                        } else if (SAFE_EVENT_LIST.contains(matchEventInfo.getEventCode())) {
                            //当前事件为reject_event拒单事件
                            //判断var收单状态:rcs:order:var:accept:=1
                            if (VarSwitchEnum.Open.getCode().equalsIgnoreCase(matchInfoService.getVarAccept(String.valueOf(matchId)))) {
                                //修改缓存var收单状态为false
                                matchInfoService.updateVarAccept(String.valueOf(matchId), VarSwitchEnum.Close.getCode());
                                //VAR订单发送等待mq：RCS_VAR_ORDER_REJECT
                                matchInfoService.sendVarOrderStatus(linkId, String.valueOf(matchId), String.valueOf(sportId),
                                        OrderStatusEnum.ORDER_ACCEPT.getCode());
                                log.info("::{}::赛事ID:{},当前事件为:{},安全事件退出VAR收单", linkId, matchId, matchEventInfo.getEventCode());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("linkId::{}::,赛事ID:{},rcs_reject_event_info_var_group消费MATCH_EVENT_INFO_TO_RISK异常,参数={},", matchId, linkId, message, e);
        }
    }

    /**
     * 查询标准赛事
     *
     */
    private StandardMatchInfo getMatchInfo(String matchId) {
        if (matchCache.size() > 1000) {
            matchCache.clear();
        }
        if (matchCache.get(matchId) != null) {
            return matchCache.get(matchId);
        } else {
            StandardMatchInfo info = standardMatchInfoMapper.selectById(matchId);
            if (info != null) {
                matchCache.put(matchId, info);
            }
            return info;
        }
    }

}
