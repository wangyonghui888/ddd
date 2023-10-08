package com.panda.sport.rcs.trade.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.dto.RedisCacheSyncBean;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.constants.TradeConstant;
import com.panda.sport.rcs.pojo.dto.ClearDTO;
import com.panda.sport.rcs.trade.mq.RcsConsumer;
import com.panda.sport.rcs.trade.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 清除统计跳分次数
 *
 * @author black
 */
@Component
@Slf4j
@RocketMQMessageListener(
        topic = "CLEAR_FOOTBALL_JUMP_TIMES",
        consumerGroup = "RCS_TRADE_CLEAR_FOOTBALL_JUMP_TIMES",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class ClearFootBallJumpTimesConsumer extends RcsConsumer<ClearDTO> {

    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;
    @Autowired
    RedisClient redisClient;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Override
    protected String getTopic() {
        return "CLEAR_FOOTBALL_JUMP_TIMES";
    }

    @Override
    public Boolean handleMs(ClearDTO clearDTO) {
        try {
            log.info("::{}::CLEAR_FOOTBALL_JUMP_TIMES", CommonUtil.getRequestId(clearDTO.getMatchId()));
            if (SportIdEnum.isFootball(clearDTO.getSportId()) && !CollectionUtils.isEmpty(clearDTO.getPlayIds())){
                QueryWrapper<StandardSportMarket> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(StandardSportMarket::getStandardMatchInfoId,clearDTO.getMatchId());
                queryWrapper.lambda().in(StandardSportMarket ::getMarketCategoryId,clearDTO.getPlayIds());
                List<StandardSportMarket> list = standardSportMarketMapper.selectList(queryWrapper);
                if (!CollectionUtils.isEmpty(list)){
                    for (StandardSportMarket standardSportMarket : list) {
                        //redisClient.hashRemove(String.format(TradeConstant.RCS_COUNT_TIMES,standardSportMarket.getStandardMatchInfoId()),standardSportMarket.getId().toString());
                        //waldkir-redis集群-发送至risk进行delete
                        String tag = standardSportMarket.getStandardMatchInfoId()+"_"+standardSportMarket.getId().toString();
                        String linkId = tag + "_" + System.currentTimeMillis();
                        String key = String.format(TradeConstant.RCS_COUNT_TIMES,standardSportMarket.getStandardMatchInfoId());
                        RedisCacheSyncBean syncBean = RedisCacheSyncBean.build(key, key, standardSportMarket.getId().toString());
                        log.info("::{}::,发送MQ消息linkId={}",standardSportMarket.getId().toString(), syncBean);
                        producerSendMessageUtils.sendMessage("RCS_RISK_REDIS_CACHE_SYNC", tag, linkId, syncBean);
                    }
                }
            }
        } catch (Exception e) {
            log.error("::{}::CLEAR_FOOTBALL_JUMP_TIMES:{}",CommonUtil.getRequestId(),e.getMessage(), e);
        }
        return true;
    }
}
