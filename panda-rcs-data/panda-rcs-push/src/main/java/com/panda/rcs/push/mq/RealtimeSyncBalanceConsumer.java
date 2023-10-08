package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.vo.statistics.BalanceVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @Description: 平衡值/盘口关联/自动水差
 * Topic=REALTIME_SYNC_BALANCE_TOPIC REALTIME_SYNC_BALANCE_TAG
 * Group=RCS_PUSH_REALTIME_SYNC_BALANCE_TAG_GROUP
 * 对应指令 -> 30007
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "REALTIME_SYNC_BALANCE_TOPIC",
        selectorExpression = "REALTIME_SYNC_BALANCE_TAG",
        consumerGroup = "RCS_PUSH_REALTIME_SYNC_BALANCE_TAG_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY,
        selectorType = SelectorType.TAG)
public class RealtimeSyncBalanceConsumer implements RocketMQListener<BalanceVo>, RocketMQPushConsumerLifecycleListener {

    //冠军玩法集合
    private List<Long> championPlayLists = Arrays.asList(10001L, 10002L, 10003L, 10004L, 10005L, 10006L, 10007L, 10008L, 10009L, 10010L, 10011L, 10012L, 10013L, 10014L, 10015L, 10016L, 10017L);

    private static final SubscriptionEnums subscriptionEnums = SubscriptionEnums.MATCH_CLEANUP_LINK_DATA;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(96);
        defaultMQPushConsumer.setConsumeThreadMax(128);
    }
    @Override
    public void onMessage(BalanceVo balanceVo) {
        if(balanceVo == null){
            return;
        }

        log.info("::{}::,::{}::平衡值/盘口关联/自动水差-盘口Id={},消费数据->{}", balanceVo.getMatchId(), balanceVo.getMarketCategoryId(),JSONObject.toJSON(balanceVo));

        //过滤冠军赛事
        if(championPlayLists.contains(balanceVo.getMarketCategoryId())){
            log.info("::{}::,::{}::冠军赛事不推送-盘口Id={}", balanceVo.getMatchId(), balanceVo.getMarketCategoryId());
            return;
        }

        String msgId = UUID.randomUUID().toString();
        List<String> playIds = new ArrayList<>();
        playIds.add(Long.toString(balanceVo.getMarketCategoryId()));

        clientManageService.sendMessage(subscriptionEnums, Long.toString(balanceVo.getMatchId()), playIds, ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), balanceVo, 0, balanceVo.getGlobalId(), msgId, null));
    }
}
