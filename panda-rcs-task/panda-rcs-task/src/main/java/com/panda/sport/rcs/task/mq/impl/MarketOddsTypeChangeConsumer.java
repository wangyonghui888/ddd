package com.panda.sport.rcs.task.mq.impl;

import static com.panda.sport.rcs.common.MqConstants.MARKET_ODDSTYPE_CHANGE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.panda.sport.rcs.task.mq.bean.*;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.MacthStatusEnum;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.utils.NameExpressionValueUtils;
import com.panda.sport.rcs.utils.OddsDiffOddsUtils;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;

import lombok.extern.slf4j.Slf4j;

/**
 * 赛事比赛阶段 match_period更新
 *
 * @author black
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "MARKET_ODDSTYPE_CHANGE",
        consumerGroup = "rcs_task_MARKET_ODDSTYPE_CHANGE",
        consumeThreadMax = 512,
        consumeTimeout = 10000L)
public class MarketOddsTypeChangeConsumer implements RocketMQListener<MarketMessageMqBean> {

	@Autowired
	MongoTemplate mongotemplate;
	
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;


    @Override
    public void onMessage(MarketMessageMqBean marketMessageMqBean) {
        log.info("MarketOddsTypeChangeConsumer接收参数:" + JsonFormatUtils.toJson(marketMessageMqBean));
        try {
        	Long matchId = marketMessageMqBean.getStandardMatchInfoId();
        	Query query = new Query();
        	query.addCriteria(Criteria.where("matchId").is(matchId));
            MatchMarketLiveBean one = mongotemplate.findOne(query, MatchMarketLiveBean.class);
            if(one == null) {
            	log.warn("mongodb没有查到赛事，不做处理{}",JSONObject.toJSONString(marketMessageMqBean));
            	return ;
            }
            
            StandardMatchMarketMessageSBean data = new StandardMatchMarketMessageSBean();
            Request<StandardMatchMarketMessageSBean> marketMessageSBeanRequest = new Request<>();
            marketMessageSBeanRequest.setLinkId(UUID.randomUUID().toString());
            marketMessageSBeanRequest.setData(data);
            data.setStandardMatchInfoId(matchId);
            data.setStatus(MacthStatusEnum.getEnum(one.getMatchStatus()).getStatus());
            List<MatchMarketLiveOddsVo.MatchMarketVo> marketVos = marketMessageMqBean.getMatchMarketVos();
            data.setMarketList(new ArrayList<StandardMarketMessageSBean>());

            marketVos.forEach(marketVo -> {
                StandardMarketMessageSBean marketMessage = new StandardMarketMessageSBean();
                data.getMarketList().add(marketMessage);

                marketMessage.setId(marketVo.getId());
                marketMessage.setStandardMatchInfoId(matchId);
                marketMessage.setMarketCategoryId(marketVo.getMarketCategoryId());
                marketMessage.setMarketType(marketVo.getMarketType());
                marketMessage.setAddition1(marketVo.getAddition1());
                marketMessage.setAddition2(marketVo.getAddition2());
                marketMessage.setOddsMetric(OddsDiffOddsUtils.getBeforeChangeDiff(marketVo.getStatus(), marketVo.getDiffOdds()));
                marketMessage.setModifyTime(System.currentTimeMillis());
                marketMessage.setMarketOddsList(new ArrayList<StandardMarketOddsMessageSBean>());
                marketMessage.setStatus(marketVo.getStatus());

                marketVo.getOddsFieldsList().forEach(matchMarketOddsFieldVo -> {
                    StandardMarketOddsMessageSBean marketOdds = new StandardMarketOddsMessageSBean();
                    marketMessage.getMarketOddsList().add(marketOdds);
                    marketOdds.setActive(matchMarketOddsFieldVo.getActive());
                    marketOdds.setOddsFieldsTemplateId(matchMarketOddsFieldVo.getNameCode());
                    marketOdds.setId(matchMarketOddsFieldVo.getId());
                    marketOdds.setOddsType(matchMarketOddsFieldVo.getOddsType());
                    marketOdds.setTargetSide(matchMarketOddsFieldVo.getTargetSide());
                    marketOdds.setMarketId(marketVo.getId());
                    if(matchMarketOddsFieldVo.getFieldOddsValue() != null) 
                    	marketOdds.setPaOddsValue(Integer.parseInt(matchMarketOddsFieldVo.getFieldOddsValue()));
                    String nameExpressionValue = NameExpressionValueUtils.getNameExpressionValue(marketMessage.getMarketCategoryId().intValue(), matchMarketOddsFieldVo.getOddsType(), marketMessage.getAddition1());
                    marketOdds.setNameExpressionValue(nameExpressionValue);
                });
            });
            producerSendMessageUtils.sendMessage(MqConstants.RCS_REALTIME_SYNC_MARKET_ODDS_TOPIC,String.valueOf(matchId),marketMessageSBeanRequest.getLinkId(), marketMessageSBeanRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
