package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.bean.CustomizedEventBean;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.MqMessageVo;
import com.panda.rcs.push.service.MatchEventTypeService;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.bean.RcsMatchEventTypeInfo;
import com.panda.sport.rcs.constants.MatchEventEnum;
import com.panda.sport.rcs.mapper.SystemItemDictMapper;
import com.panda.sport.rcs.pojo.MatchEventType;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;
import com.panda.sport.rcs.pojo.SystemItemDict;
import com.panda.sport.rcs.pojo.dto.MatchEventInfoDTO;
import com.panda.sport.rcs.wrapper.MatchStatisticsInfoDetailService;
import com.panda.sport.rcs.wrapper.RcsMatchEventTypeInfoService;
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

import java.util.Arrays;
import java.util.List;

/**
 * @Description: 推送三方事件流
 * Topic=MATCH_EVENT_INFO_TO_RISK
 * Group=RCS_PUSH_MATCH_EVENT_INFO_TO_RISK
 * 对应指令-> 30013
 * 客户端订阅：{"needCommands":[30013],"protocolVersion":2,"subscribe":{"30013":[{"matchId":"3001820","dataSourceCode":"SR"}]},"uuid":"b9c1b977-7b37-412f-a4f1-dce19c447461"}
 * message:{"linkId":"RB_0af51540202112312009216652de7922ad6f","data":[{"id":1476888203774259201,"sportId":1,"canceled":0,"dataSourceCode":"RB","sourceType":1,"eventCode":"safe_ball","eventTime":1640952561665,"extraInfo":"","homeAway":"home","matchPeriodId":6,"matchType":1,"playerIdPrefix":null,"player1Id":null,"player1Name":null,"player2Id":null,"player2Name":null,"secondsFromStart":1611,"standardMatchId":1699762,"standardTeamId":24009,"t1":0,"t2":0,"secondNum":null,"firstT1":null,"firstT2":null,"secondT1":null,"secondT2":null,"firstNum":null,"thirdEventId":"558","thirdMatchId":"1476100829292875777","thirdMatchSourceId":"1569682","thirdTeamId":null,"remark":null,"periodRemainingSeconds":null,"penaltyShootoutRound":null,"createTime":1640952561692,"modifyTime":1640952561692,"addition6":null,"addition7":null,"addition8":null,"addition9":null,"addition10":null,"addition1":null,"addition2":null,"addition3":null,"addition4":null,"addition5":null,"isErrorEndEvent":0}],"dataSourceTime":1640952561696,"dataSourceCode":"RB","dataType":null,"tag":null,"operaterId":null}
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "MATCH_EVENT_INFO_TO_RISK",
        consumerGroup = "RCS_PUSH_MATCH_EVENT_INFO_TO_RISK",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class ThirdMatchEventConsumer implements RocketMQListener<MqMessageVo<List<MatchEventInfoDTO>>>, RocketMQPushConsumerLifecycleListener {

    private static final String canceled = "取消";

    private static final SubscriptionEnums subscriptionEnums = SubscriptionEnums.THIRD_MATCH_EVENT;

    public static List sportIds = Arrays.asList(3,4,5,7,8,9,10);
    //CF数据不过滤的
    private static List<String> unCFFilterEvent = Arrays.asList("kick_off_team",
            "goal","canceled_goal","possible_goal",
            "corner","canceled_corner","possible_corner",
            "red_card","yellow_card","canceled_red_card","canceled_yellow_card","possible_red_card","possible_yellow_card",
            "video_assistant_referee","possible_video_assistant_referee");
    private static List<String> unCFPeriod = Arrays.asList("7","31","100");

    @Autowired
    MatchStatisticsInfoDetailService matchStatisticsInfoDetailService;

    @Autowired
    RcsMatchEventTypeInfoService rcsMatchEventTypeInfoService;

    @Autowired
    MatchEventTypeService matchEventTypeService;

    @Autowired
    SystemItemDictMapper systemItemDictMapper;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(32);
        defaultMQPushConsumer.setConsumeThreadMax(64);
    }

    @Override
    public void onMessage(MqMessageVo<List<MatchEventInfoDTO>> listMqMessage) {
        //所有数据源 直接推送 无需过滤- 30013
        if(listMqMessage.getData() == null){
            return;
        }
        MatchEventInfoDTO matchEventInfo = listMqMessage.getData().get(0);
        log.info("::{}::三方事件流-赛事Id={}", listMqMessage.getLinkId(), matchEventInfo.getStandardMatchId());

        CustomizedEventBean customizedEventBean = transferEventMsg(matchEventInfo);
        clientManageService.sendMessage(subscriptionEnums, listMqMessage.getDataSourceCode(), Long.toString(matchEventInfo.getStandardMatchId()), ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), customizedEventBean, 0, listMqMessage.getLinkId(), listMqMessage.getLinkId(), null));
    }

    /**
     * 封装推送数据
     * @param matchEventInfoDTO
     * @return
     */
    private CustomizedEventBean transferEventMsg(MatchEventInfoDTO matchEventInfoDTO) {
        CustomizedEventBean bean = new CustomizedEventBean();
        bean.setEventCode(matchEventInfoDTO.getEventCode());
        bean.setDataSourceCode(matchEventInfoDTO.getDataSourceCode());
        bean.setHomeAway(matchEventInfoDTO.getHomeAway());
        bean.setSportId(matchEventInfoDTO.getSportId());
        bean.setEventTime(matchEventInfoDTO.getEventTime());
        bean.setCurrentTime(matchEventInfoDTO.getSecondsFromStart());
        bean.setStandardMatchId(matchEventInfoDTO.getStandardMatchId());
        bean.setRemark(matchEventInfoDTO.getRemark());
        if (matchEventInfoDTO.getSportId() == 1) {
            RcsMatchEventTypeInfo type = rcsMatchEventTypeInfoService.getOneInfo(matchEventInfoDTO);
            if (type != null) {
                bean.setEventName(type.getEventName());
                bean.setEventType(type.getEventType());
                bean.setEventEnName(type.getEventEnName());
            }
        } else {
            LambdaQueryWrapper<MatchEventType> lambdaQueryWrapper = new QueryWrapper<MatchEventType>().lambda();
            lambdaQueryWrapper.eq(MatchEventType::getEventCode, matchEventInfoDTO.getEventCode());
            lambdaQueryWrapper.eq(MatchEventType::getSportId, matchEventInfoDTO.getSportId());
            lambdaQueryWrapper.select(MatchEventType::getEventName);
            MatchEventType type = matchEventTypeService.getOne(lambdaQueryWrapper);
            if (type != null) bean.setEventName(type.getEventName());
        }

        //比赛阶段
        if ("match_status".equals(matchEventInfoDTO.getEventCode())) {
            LambdaQueryWrapper<SystemItemDict> query = new QueryWrapper<SystemItemDict>().lambda();
            query.eq(SystemItemDict::getParentTypeId, 8);
            query.eq(SystemItemDict::getValue, matchEventInfoDTO.getExtraInfo());
            query.eq(SystemItemDict::getAddition1, matchEventInfoDTO.getSportId());
            query.select(SystemItemDict::getDescription, SystemItemDict::getRemark);
            List<SystemItemDict> list = systemItemDictMapper.selectList(query);
            if (list != null && list.size() > 0) {
                bean.setEventName(list.get(0).getDescription());
                bean.setEventEnName(list.get(0).getRemark());
            }
        }

        LambdaQueryWrapper<MatchStatisticsInfo> lambda = new QueryWrapper<MatchStatisticsInfo>().lambda();
        lambda.eq(MatchStatisticsInfo::getStandardMatchId, matchEventInfoDTO.getStandardMatchId());
        lambda.select(MatchStatisticsInfo::getScore, MatchStatisticsInfo::getSecondsMatchStart);
        String score = matchStatisticsInfoDetailService.queryMatchScore(matchEventInfoDTO.getStandardMatchId());
        if (StringUtils.isNotBlank(score)) {
            bean.setScore(score);
        }
        if (MatchEventEnum.Goal.getCode().equals(matchEventInfoDTO.getEventCode()) || "score_change".equals(matchEventInfoDTO.getEventCode()) ||
                "score_correction".equals(matchEventInfoDTO.getEventCode())) {
            bean.setScore(matchEventInfoDTO.getT1() + ":" + matchEventInfoDTO.getT2());
        }
        if (sportIds.contains(matchEventInfoDTO.getSportId().intValue())) {
            bean.setScore(matchEventInfoDTO.getT1() + ":" + matchEventInfoDTO.getT2());
            bean.setSetScore(matchEventInfoDTO.getFirstT1() + ":" + matchEventInfoDTO.getFirstT2());
            bean.setCurrentScore(matchEventInfoDTO.getSecondT1() + ":" + matchEventInfoDTO.getSecondT2());
        }
        if (StringUtils.isBlank(bean.getScore()) || bean.getScore().contains("null")) bean.setScore("0:0");
        if (matchEventInfoDTO.getCanceled() != null && matchEventInfoDTO.getCanceled() == 1) {
            bean.setEventName(canceled + bean.getEventName());
            bean.setEventEnName("Cancel " + bean.getEventEnName());
        }
        if (matchEventInfoDTO.getEventCode().equals("time_start")) {
            if (matchEventInfoDTO.getExtraInfo() != null && matchEventInfoDTO.getExtraInfo().equals("1")) {
                bean.setEventName("时钟计时开始");
            } else {
                bean.setEventName("时钟计时停止");
            }
        }
        return bean;
    }
}
