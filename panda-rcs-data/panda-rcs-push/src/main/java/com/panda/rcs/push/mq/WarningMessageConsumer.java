package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.LanguageEnums;
import com.panda.rcs.push.entity.enums.SportEnum;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.pojo.RcsBroadCast;
import com.panda.sport.rcs.pojo.dto.RcsBroadCastDTO;
import com.panda.sport.rcs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 操盘消息
 * Topic=rcs_predict_odds_data_ws
 * Group=RCS_PUSH_predict_odds_data_ws_GROUP
 * 对应指令 -> 30040
 * 数据：{"matchType":1,"rcsBroadCast":{"content":"{\"zs\":\"万丹芝勒贡 VS 佩斯梭罗(4395890361541697827):全场大小玩法2.25盘口货量出涨超警戒值45826元。\",\"en\":\"Rans Cilegon FC VS Persis Solo(4395890361541697827):Total play 2.25 market stock exceeded by ¥45826。\"}","extendsField":"1697827","extendsField1":"1","id":754741,"msgId":"c6e4eec5-992b-4a6b-a619-cd95919671e5","msgType":1,"status":1},"sportId":1,"userId":[330,408,86,295,28,178,393,394,505,511,388,272,17,476,414,23,27,473,395,535,666,510,446,454,686,667,688,663,726,377,731,94,700,714,358,600,29,6,329,703,701,708,162,715,656,391,80,702,658,180,422,330,0,661]}
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_predict_odds_data_ws",
        consumerGroup = "RCS_PUSH_predict_odds_data_ws_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class WarningMessageConsumer implements RocketMQListener<RcsBroadCastDTO>, RocketMQPushConsumerLifecycleListener {

    private static final SubscriptionEnums subscriptionEnums = SubscriptionEnums.TRADER_MESSAGE;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(16);
        defaultMQPushConsumer.setConsumeThreadMax(32);
    }

    @Override
    public void onMessage(RcsBroadCastDTO broadCast) {
        log.info("::{}::,::{}::操盘消息消费数据->{}", broadCast.getMsgId(), broadCast.getMatchId(), JSONObject.toJSON(broadCast));

        if(broadCast.getSportId().intValue() == SportEnum.SPORT_BASKETBALL.getKey()){
            log.info("::{}::,::{}::操盘消息-篮球消息不发-LinkId={}", broadCast.getMsgId(),broadCast.getMatchId());
            return;
        }

        List<String> list = new ArrayList<>();
        if(broadCast.getUserId() != null && broadCast.getUserId().size() > 0){
            broadCast.getUserId().forEach(uid -> {
                list.add(uid.toString());
            });
        }

        JSONObject jsonContent = new JSONObject();
        if (broadCast.getRcsBroadCast().getContent() != null && jsonValid(broadCast.getRcsBroadCast().getContent())){
            jsonContent = JSONObject.parseObject(broadCast.getRcsBroadCast().getContent());
        }

        Object zsMessage = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), getPushData(broadCast, jsonContent, LanguageEnums.LANGUAGE_CHINA.getKey()), 0, broadCast.getMsgId(), broadCast.getMsgId(), null);
        Object enMessage = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), getPushData(broadCast, jsonContent, LanguageEnums.LANGUAGE_ENGLISH.getKey()), 0, broadCast.getMsgId(), broadCast.getMsgId(), null);

        clientManageService.sendMessage(subscriptionEnums, list, broadCast.getSportId().intValue(), broadCast.getMatchType(), zsMessage, enMessage);
    }

    /**
     * 创建推送数据
     * @param broadCast
     * @param jsonContent
     * @return
     */
    private static RcsBroadCast getPushData(RcsBroadCastDTO broadCast, JSONObject jsonContent, String languageType){
        RcsBroadCast message = new RcsBroadCast();
        message.setContent(jsonContent.get(languageType) != null ? jsonContent.get(languageType).toString() : null);
        message.setStatus(broadCast.getRcsBroadCast().getStatus());
        message.setIsRead(broadCast.getRcsBroadCast().getIsRead());
        message.setMsgType(broadCast.getRcsBroadCast().getMsgType());
        message.setMsgId(broadCast.getRcsBroadCast().getMsgId());
        message.setId(broadCast.getRcsBroadCast().getId());
        message.setExtendsField(broadCast.getRcsBroadCast().getExtendsField());
        message.setExtendsField1(broadCast.getRcsBroadCast().getExtendsField1());
        if (broadCast.getRcsBroadCast().getExtendsField1() != null && !"null".equals(broadCast.getRcsBroadCast().getExtendsField1()) && jsonValid(broadCast.getRcsBroadCast().getExtendsField1())) {
            if (!broadCast.getRcsBroadCast().getExtendsField1().equals("0") && !broadCast.getRcsBroadCast().getExtendsField1().equals("1") && !broadCast.getRcsBroadCast().getExtendsField1().equals("3")) {
                try {
                    Map<String, Object> stringObjectMap = JSONObject.parseObject(broadCast.getRcsBroadCast().getExtendsField1());
                    message.setExtendsField1(stringObjectMap.get(languageType).toString());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return message;
    }

    /**
     * 验证是否是标准json
     * @param content
     * @return
     */
    private static boolean jsonValid(String content) {
        try {
            JSONObject.parseObject(content);
        } catch (Exception e){
            return false;
        }

        if(StringUtils.isBlank(content) || "null".equals(content)){
            return false;
        }

        return JSONObject.isValid(content);
    }
}
