package com.panda.sport.rcs.oddin.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.sport.data.rcs.vo.oddin.StandardMarketOddsVo;
import com.panda.sport.data.rcs.vo.oddin.StandardMarketVo;
import com.panda.sport.data.rcs.vo.oddin.StandardMatchVo;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.DataSourceEnum;
import com.panda.sport.rcs.oddin.entity.common.DataRealTimeMessageBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.oddin.common.Constants.STANDAR_MATCH_MARKET_INFO_OF_ODDIN;

/**
 * @author Beulah
 * @date 2023/4/1 20:24
 * @description 监听赛事信息，盘口，赔率变动 广播到本地
 */

@Component
@Slf4j
@RocketMQMessageListener(topic = "STANDARD_MARKET_ODDS", consumerGroup = "rcs_risk_oddin_market_odds_group", messageModel = MessageModel.BROADCASTING, consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchMarketOddsConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    @Resource
    private RedisClient redisClient;

    @Override
    public void onMessage(String message) {
        try {
            //防止泛型擦除
            DataRealTimeMessageBean<StandardMatchMarketMessage> msg = JSON.parseObject(message, new TypeReference<DataRealTimeMessageBean<StandardMatchMarketMessage>>() {
            });
            StandardMatchMarketMessage matchMessage = msg.getData();
            //判断是否为OD数据源的信息
            if (matchMessage.getStandardMatchInfoId() == null || !DataSourceEnum.OD.getDataSource().equalsIgnoreCase(matchMessage.getDataSourceCode())) {
                return;
            }
            //标准赛事VO
            String matchKey = String.format(STANDAR_MATCH_MARKET_INFO_OF_ODDIN, matchMessage.getStandardMatchInfoId());
            String matchInfoStr = redisClient.get(matchKey);
            StandardMatchVo standardMatchVo = null;
            if (StringUtils.isBlank(matchInfoStr)) {
                //新增缓存数据
                standardMatchVo = addMatchInfo2Cache(matchMessage);
            } else {
                //修改缓存数据
                StandardMatchVo standardMatchMessage = JSON.parseObject(matchInfoStr, StandardMatchVo.class);
                standardMatchVo = udpdateMatchInfo2Cache(standardMatchMessage, matchMessage);
            }
            String key = String.format(STANDAR_MATCH_MARKET_INFO_OF_ODDIN, standardMatchVo.getStandardMatchInfoId());
            /*  RedisClient redisClient = SpringContextUtils.getBeanByClass(RedisClient.class);*/
            redisClient.setExpiry(key, JSON.toJSONString(standardMatchVo), 2 * 60 * 60 * 1000L);
//            log.info("::matchId:{}::缓存赛事信息成功:key:{}:：value:{}", standardMatchVo.getStandardMatchInfoId(), key, JSONObject.toJSONString(standardMatchVo));
        } catch (Exception e) {
            log.info("监听赛事信息，盘口，赔率变动出错：", e);
        }
    }

    /**
     * 更新赛事/盘口/投注项数据到缓存
     *
     * @param standardMatchMessage 缓存中的赛事/盘口数据
     * @param matchMessage         mq推送的赛事/盘口
     */
    private StandardMatchVo udpdateMatchInfo2Cache(StandardMatchVo standardMatchVo, StandardMatchMarketMessage matchMessage) {
        StandardMatchVo matchVo = addMatchInfo2Cache(matchMessage);
        List<StandardMarketVo> marketVoList = matchVo.getMarketList();
        if (CollectionUtils.isNotEmpty(marketVoList)) {
            Map<Long, StandardMarketVo> map = new HashMap<>();
            if (CollectionUtils.isNotEmpty(standardMatchVo.getMarketList())) {
                map = standardMatchVo.getMarketList().stream().collect(Collectors.toMap(StandardMarketVo::getMarketId, standardMarketVo -> standardMarketVo));
            }
            for (StandardMarketVo marketVo : marketVoList) {
                StandardMarketVo cacheVo = map.get(marketVo.getMarketId());
                if (Objects.nonNull(cacheVo)) {
                    map.remove(cacheVo.getMarketId());
                }
            }
            Set<Map.Entry<Long, StandardMarketVo>> entries = map.entrySet();
            for (Map.Entry<Long, StandardMarketVo> entry : entries) {
                marketVoList.add(entry.getValue());
            }
            matchVo.setMarketList(marketVoList);
        }
        return matchVo;
    }

    /**
     * 新增赛事/盘口/投注项数据到缓存
     *
     * @param matchMessage
     */
    private StandardMatchVo addMatchInfo2Cache(StandardMatchMarketMessage matchMessage) {
        StandardMatchVo standardMatchVo = new StandardMatchVo();
        BeanCopyUtils.copyProperties(matchMessage, standardMatchVo);
        //缓存盘口数据
        List<StandardMarketMessage> marketList = matchMessage.getMarketList();
        if (CollectionUtils.isNotEmpty(marketList)) {
            //标准盘口Vo集合
            List<StandardMarketVo> marketVoList = new ArrayList<>();
            for (StandardMarketMessage marketMessage : marketList) {
                StandardMarketVo standardMarketVo = new StandardMarketVo();
                //盘口id
                standardMarketVo.setMarketId(marketMessage.getId());
                //盘口状态
                standardMarketVo.setStatus(marketMessage.getStatus());
                //盘口投注项集合
                List<StandardMarketOddsMessage> marketOddsList = marketMessage.getMarketOddsList();
                List<StandardMarketOddsVo> oddsVoList = new ArrayList<>();
                for (StandardMarketOddsMessage standardMarketMessage : marketOddsList) {
                    StandardMarketOddsVo standardMarketOddsVo = new StandardMarketOddsVo();
                    //投注项ID
                    standardMarketOddsVo.setId(standardMarketMessage.getId());
                    //selectionId
                    standardMarketOddsVo.setExtraInfo(standardMarketMessage.getExtraInfo());
                    oddsVoList.add(standardMarketOddsVo);
                }
                standardMarketVo.setMarketOddsList(oddsVoList);
                marketVoList.add(standardMarketVo);
            }
            standardMatchVo.setMarketList(marketVoList);

        }
        return standardMatchVo;
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }
}
