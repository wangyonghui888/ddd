package com.panda.sport.rcs.task.mq.orderSummary;

import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.task.mq.bean.MatchCategoryUpdateBean;
import com.panda.sport.rcs.task.wrapper.RcsOrderSummaryService;
import com.panda.sport.rcs.task.wrapper.StandardSportMarketService;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
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

import java.util.List;
import java.util.Map;

/**
 * @Description mq 处理赔率
 * @Param
 * @Author kimi
 * @Date 2020/7/8
 * @return
 **/
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "STANDARD_MARKET_TYPE_UPDATE",
        consumerGroup = "rcs_task_STANDARD_MARKET_TYPE_UPDATE",
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
public class OrderSummaryConsumer implements RocketMQListener<MatchMarketLiveOddsVo.MatchMarketVo>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private RcsOrderSummaryService rcsOrderSummaryService;
    @Autowired
    private StandardSportMarketService standardSportMarketService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(512);
    }

    @Override
    public void onMessage(MatchMarketLiveOddsVo.MatchMarketVo matchMarketVo) {

    	String linkId = "OrderSummaryConsumer";
    	log.info("::{}::-TradeConfigConsumer接收参数:", linkId, JsonFormatUtils.toJson(matchMarketVo));
        try {
            StandardSportMarket standardSportMarket = standardSportMarketService.selectStandardSportMarketByMarketId(matchMarketVo.getMarketId());
            if (standardSportMarket != null && matchMarketVo.getMarketType() == 1) {
                List<MatchMarketLiveOddsVo.MatchMarketOddsFieldVo> oddsFieldsList = matchMarketVo.getOddsFieldsList();
                rcsOrderSummaryService.updateOrInsertOrOddsValueMax(oddsFieldsList, standardSportMarket.getSportId(), standardSportMarket.getStandardMatchInfoId(),
                    standardSportMarket.getMarketCategoryId(), standardSportMarket.getId());
            } else {
            	log.info("::{}::-滚球的赔率有进行发送，请处理", linkId);
            }
        } catch (Exception e) {
        	log.error("::{}::-发生异常，请处理:{}", linkId, e.getMessage(), e);
        }
        return ;
    }
}
