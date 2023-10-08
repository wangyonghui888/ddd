package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.rcs.push.utils.Gzip;
import com.panda.sport.rcs.vo.StandardTxThirdMarketOddsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Describtion 百家赔
 * @Auther jstyDC
 * @Date 2022-02-2022/2/5 14:17
 */
@Slf4j
@Component
@RefreshScope
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_MULTIPLE_ODDS",
        consumerGroup = "RCS_PUSH_MULTIPLE_ODDS_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class StandardTxThirdMarketOddsConsumer implements RocketMQListener<Request<StandardTxThirdMarketOddsVo>>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.RCS_MULTIPLE_ODDS;

    @Autowired
    private ClientManageService clientManageService;

    @Value("${StandardTxThirdMarketOdds.hasOpen:1}")
    String hasOpen;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(16);
        defaultMQPushConsumer.setConsumeThreadMax(32);
    }

    @Override
    public void onMessage(Request<StandardTxThirdMarketOddsVo> standardvo) {

        //做控制
        if(hasOpen == null || hasOpen.equals("0")){
            return;
        }

        if (standardvo == null || standardvo.getData() == null || standardvo.getData().getStandardMatchInfoId() == null || standardvo.getData().getMarketCategoryIds()==null) {
            return;
        }
        //玩法类型转换
        List<String> plays=new ArrayList<>();
        standardvo.getData().getMarketCategoryIds().stream().forEach(value->plays.add(value.toString()));
        log.info("::{}::消费百家赔数据,赛事Id={}", standardvo.getLinkId(), standardvo.getData().getStandardMatchInfoId());
        try {
            String respGzipStr = Gzip.compress(JSONObject.toJSONString(standardvo.getData()));
            clientManageService.sendMessage(subscriptionEnums, standardvo.getData().getStandardMatchInfoId(),plays, ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), respGzipStr, 0, null, UUID.randomUUID().toString(), null));
        } catch (Exception e) {
            log.error("::{}::,::{}::百家赔异常-赛事Id={}，异常信息：", standardvo.getLinkId(), standardvo.getData().getStandardMatchInfoId(), e);
        }
    }
}
